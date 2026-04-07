package com.onehundredtwentyninth.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

public class AllureIdParameterPlaceCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return new int[]{
                TokenTypes.METHOD_DEF
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[]{
                TokenTypes.METHOD_DEF
        };
    }

    @Override
    public int[] getRequiredTokens() {
        return CommonUtil.EMPTY_INT_ARRAY;
    }

    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.METHOD_DEF
                && AnnotationUtil.containsAnnotation(ast, "ParameterizedTest")
                && AnnotationUtil.containsAnnotation(ast, "MethodSource")) {
            var clazzAst = ast.getParent().getParent();
            if (AnnotationUtil.containsAnnotation(clazzAst, "ParameterizedAllureId")
                    || AnnotationUtil.containsAnnotation(ast, "ParameterizedAllureId")) {
                var parameters = ast.findFirstToken(TokenTypes.PARAMETERS);
                var firstParameter = parameters.findFirstToken(TokenTypes.PARAMETER_DEF);

                var parameterType = firstParameter.findFirstToken(TokenTypes.TYPE).getFirstChild();
                var parameterName = firstParameter.findFirstToken(TokenTypes.IDENT);

                if (!"String".equals(parameterType.getText()) || !"allureId".equals(parameterName.getText())) {
                    log(ast, "First parameter of ParameterizedTest must be allureId with String type");
                }
            }
        }
    }
}
