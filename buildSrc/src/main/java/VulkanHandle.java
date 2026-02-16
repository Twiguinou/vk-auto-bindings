import module java.base;

import com.palantir.javapoet.ClassName;
import fr.kenlek.jpgen.generator.data.*;
import fr.kenlek.jpgen.generator.data.features.GetSymbolicName;
import fr.kenlek.jpgen.generator.data.features.TypeFeature;

public sealed abstract class VulkanHandle implements Type.Delegated, Declaration
    permits VulkanBaseHandle, VulkanChildHandle
{
    private final ClassName m_path;
    private final List<FunctionDeclaration> m_functions = new ArrayList<>();

    protected VulkanHandle(ClassName path)
    {
        this.m_path = path;
    }

    protected static FunctionDeclaration stripFunction(FunctionDeclaration function)
    {
        return new FunctionDeclaration(
            function.name(),
            new FunctionType(
                function.type().returnType(),
                function.type().parameterTypes().subList(1, function.type().parameterTypes().size())
            ),
            function.parameterInfos().subList(1, function.parameterInfos().size())
        );
    }

    @Override
    public ClassName path()
    {
        return this.m_path;
    }

    @Override
    public Type underlying()
    {
        return MiscType.POINTER;
    }

    public List<FunctionDeclaration> functions()
    {
        return Collections.unmodifiableList(this.m_functions);
    }

    public void register(FunctionDeclaration function)
    {
        if (function.type().parameterTypes().isEmpty() || !function.type().parameterTypes().getFirst().equals(this))
        {
            throw new IllegalArgumentException("This function does not belong to this handle type: " + function);
        }

        this.m_functions.add(function);
    }

    public void reset()
    {
        this.m_functions.clear();
    }

    @Override
    public <T> T apply(TypeFeature<T> feature)
    {
        return feature.check(switch (feature)
        {
            case GetSymbolicName _ -> this.symbolicName();
            default -> Delegated.super.apply(feature);
        });
    }
}
