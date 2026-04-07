# checkstyle-plugin-custom-checks

Плагин с кастомными проверками Checkstyle для валидации проектных правил в Java-тестах.

## О проекте

Этот проект содержит проверки для Checkstyle, которые помогают валидировать соглашения в тестовом коде, не покрываемые стандартными правилами Checkstyle.

Цель плагина - сделать тесты более единообразными и автоматически контролировать важные для команды договоренности, например:

- наличие обязательных аннотаций у тестов
- порядок аннотаций у классов и методов
- корректное расположение параметра `allureId` в параметризованных тестах

## Пример конфигурации

Пример подключения проверок в конфигурации Checkstyle:

```xml
<module name="Checker">
    <module name="TreeWalker">
        <module name="CustomAnnotationOrderCheck">
            <property name="customAnnotationOrderRules" value="Epic Feature Story DisplayName"/>
        </module>

        <module name="MissingRequiredTestAnnotationsCheck">
            <property name="requiredAnnotationsRules" value="Owner Feature"/>
        </module>

        <module name="AllureIdParameterPlaceCheck"/>
    </module>
</module>
```