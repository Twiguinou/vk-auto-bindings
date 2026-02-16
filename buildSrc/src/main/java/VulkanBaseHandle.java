import module java.base;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.TypeSpec;
import fr.kenlek.jpgen.api.dynload.NativeProxies;
import fr.kenlek.jpgen.generator.data.FunctionDeclaration;
import fr.kenlek.jpgen.generator.data.features.GetType;

import static javax.lang.model.element.Modifier.*;

public final class VulkanBaseHandle extends VulkanHandle
{
    private static final ClassName INDIRECT_ANNOTATION_PATH = ClassName.get("fr.kenlek.vulkan", "IndirectCommand");
    private static final ClassName HANDLE_DOWNCALL_DISPATCHER_PATH = ClassName.get("fr.kenlek.vulkan", "HandleDowncallDispatcher");

    private final ClassName m_interfacePath;
    public final String symbolFunctionName;
    private final List<FunctionDeclaration> m_indirectFunctions = new ArrayList<>();

    public VulkanBaseHandle(ClassName path, ClassName interfacePath, String symbolFunctionName)
    {
        super(path);
        this.m_interfacePath = interfacePath;
        this.symbolFunctionName = symbolFunctionName;
    }

    public List<FunctionDeclaration> indirectFunctions()
    {
        return Collections.unmodifiableList(this.m_indirectFunctions);
    }

    public void registerIndirect(FunctionDeclaration function)
    {
        this.m_indirectFunctions.add(function);
    }

    @Override
    public void reset()
    {
        super.reset();
        this.m_indirectFunctions.clear();
    }

    @Override
    public Optional<TypeSpec> define(ClassName layouts)
    {
        return Optional.of(TypeSpec.interfaceBuilder(this.path())
            .addModifiers(PUBLIC)
            .addMethod(MethodSpec.methodBuilder("load")
                .addModifiers(PUBLIC, STATIC)
                .returns(this.path())
                .addParameter(this.m_interfacePath, "vulkan")
                .addParameter(MemorySegment.class, "handle")
                .addStatement(
                    "return $1T.make($2T.lookup(), $3T.class, new $4T(handle, $4T.symbolLookup(pName -> vulkan.$5L(handle, pName))))",
                    NativeProxies.class, MethodHandles.class, this.path(), HANDLE_DOWNCALL_DISPATCHER_PATH, this.symbolFunctionName
                )
                .build())
            .addMethod(MethodSpec.methodBuilder("get")
                .addModifiers(PUBLIC, ABSTRACT)
                .returns(MemorySegment.class)
                .build())
            .addMethods(this.functions().stream()
                .map(function ->
                {
                    FunctionDeclaration stripped = stripFunction(function);
                    return MethodSpec.methodBuilder(stripped.name())
                        .addModifiers(PUBLIC, ABSTRACT)
                        .returns(stripped.type().returnType().apply(new GetType(GetType.Target.HEADER_RETURN, layouts)))
                        .addParameters(stripped.type().parameterSpecs(stripped.parameterInfos(), GetType.Target.HEADER_PARAMETER, layouts))
                        .build();
                })
                .toList())
            .addMethods(this.indirectFunctions().stream()
                .map(function -> MethodSpec.methodBuilder(function.name())
                    .addAnnotation(INDIRECT_ANNOTATION_PATH)
                    .addModifiers(PUBLIC, ABSTRACT)
                    .returns(function.type().returnType().apply(new GetType(GetType.Target.HEADER_RETURN, layouts)))
                    .addParameters(function.type().parameterSpecs(function.parameterInfos(), GetType.Target.HEADER_PARAMETER, layouts))
                    .build())
                .toList())
            .build()
        );
    }
}
