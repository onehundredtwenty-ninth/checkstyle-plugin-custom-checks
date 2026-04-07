package com.onehundredtwentyninth.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

import java.util.*;

public class MissingRequiredTestAnnotationsCheck extends AbstractCheck {

    private final Set<String> requiredAnnotationsRules = new LinkedHashSet<>();

    public int[] getDefaultTokens() {
        return new int[]{
                TokenTypes.CLASS_DEF,
                TokenTypes.METHOD_DEF
        };
    }

    public int[] getAcceptableTokens() {
        return new int[]{
                TokenTypes.CLASS_DEF,
                TokenTypes.METHOD_DEF
        };
    }

    public int[] getRequiredTokens() {
        return CommonUtil.EMPTY_INT_ARRAY;
    }

    public final void setRequiredAnnotationsRules(final String inputRequiredAnnotations) {
        requiredAnnotationsRules.addAll(Arrays.asList(
                inputRequiredAnnotations.trim().replaceAll(" +", " ").split(" "))
        );
    }

    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.METHOD_DEF
                && AnnotationUtil.containsAnnotation(ast, Set.of("Test", "ParameterizedTest"))) {
            var clazzAst = ast.getParent().getParent();
            requiredAnnotationsRules.forEach(s -> {
                var isClazzContainsAnnotations = containsAnnotationIncludeRepeatable(clazzAst, s);
                var isMethodContainsAnnotations = containsAnnotationIncludeRepeatable(ast, s);
                if (!isClazzContainsAnnotations && !isMethodContainsAnnotations) {
                    log(ast, "Missing requiredAnnotation: " + s);
                }
            });
        }
    }

    public boolean containsAnnotationIncludeRepeatable(DetailAST ast, String annotationName) {
        if (AnnotationUtil.containsAnnotation(ast, annotationName)) {
            return true;
        } else {
            var annotationHolder = AnnotationUtil.getAnnotationHolder(ast);
            var repeatableAnnotations = findRepeatableAnnotations(annotationHolder);
            return repeatableAnnotations.stream()
                    .map(this::getAnnotationFullIdent)
                    .toList()
                    .contains(annotationName);
        }
    }

    private List<DetailAST> findRepeatableAnnotations(final DetailAST holder) {
        List<DetailAST> result = new ArrayList<>();
        for (DetailAST child = holder.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.findFirstToken(TokenTypes.ANNOTATION_ARRAY_INIT) != null) {
                result.add(child.findFirstToken(TokenTypes.ANNOTATION_ARRAY_INIT).getFirstChild());
            }
        }
        return result;
    }

    private String getAnnotationFullIdent(DetailAST annotationNode) {
        final DetailAST identNode = annotationNode.findFirstToken(TokenTypes.IDENT);
        final String annotationString;

        if (identNode == null) {
            final DetailAST dotNode = annotationNode.findFirstToken(TokenTypes.DOT);
            annotationString = FullIdent.createFullIdent(dotNode).getText();
        } else {
            annotationString = identNode.getText();
        }

        return annotationString;
    }
}
