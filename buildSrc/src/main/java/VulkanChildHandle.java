import module java.base;

import com.palantir.javapoet.*;
import fr.kenlek.jpgen.generator.data.FunctionDeclaration;
import fr.kenlek.jpgen.generator.data.features.GetType;

import static javax.lang.model.element.Modifier.PUBLIC;

public final class VulkanChildHandle extends VulkanHandle
{
    private final VulkanBaseHandle m_parent;

    public VulkanChildHandle(ClassName path, VulkanBaseHandle parent)
    {
        super(path);
        this.m_parent = parent;
    }

    @Override
    public void register(FunctionDeclaration function)
    {
        super.register(function);
        this.m_parent.registerIndirect(function);
    }

    @Override
    public Optional<TypeSpec> define(ClassName layouts)
    {
        return Optional.of(TypeSpec.recordBuilder(this.path())
            .addModifiers(PUBLIC)
            .recordConstructor(MethodSpec.constructorBuilder()
                .addParameter(MemorySegment.class, "handle")
                .addParameter(this.m_parent.path(), "parent")
                .build())
            .addMethods(this.functions().stream()
                .map(function ->
                {
                    FunctionDeclaration stripped = VulkanHandle.stripFunction(function);
                    TypeName returnType = stripped.type().returnType().apply(new GetType(GetType.Target.HEADER_RETURN, layouts));
                    List<ParameterSpec> parameterSpecs = stripped.type().parameterSpecs(stripped.parameterInfos(), GetType.Target.HEADER_PARAMETER, layouts);
                    return MethodSpec.methodBuilder(stripped.name())
                        .addModifiers(PUBLIC)
                        .returns(returnType)
                        .addParameters(parameterSpecs)
                        .addStatement("$Lthis.parent().$L($L)",
                            returnType.equals(TypeName.VOID) ? "" : "return ", stripped.name(),
                            Stream.concat(Stream.of("this.handle()"), parameterSpecs.stream().map(ParameterSpec::name))
                                .collect(Collectors.joining(", ")))
                        .build();
                })
                .toList())
            .build()
        );
    }
}
