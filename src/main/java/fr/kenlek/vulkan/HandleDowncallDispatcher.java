package fr.kenlek.vulkan;

import module java.base;

import fr.kenlek.jpgen.api.dynload.LinkingDowncallDispatcher;

import static fr.kenlek.jpgen.api.ForeignUtils.prependAPIName;
import static java.lang.foreign.MemorySegment.NULL;
import static java.lang.foreign.ValueLayout.ADDRESS;

public class HandleDowncallDispatcher extends LinkingDowncallDispatcher
{
    public final MemorySegment handle;

    public HandleDowncallDispatcher(MemorySegment handle, Linker linker, SymbolLookup lookup)
    {
        super(linker, lookup);
        this.handle = handle;
    }

    public HandleDowncallDispatcher(MemorySegment handle, SymbolLookup lookup)
    {
        super(lookup);
        this.handle = handle;
    }

    public static SymbolLookup symbolLookup(Function<MemorySegment, MemorySegment> getProcAddr)
    {
        return name ->
        {
            try (Arena arena = Arena.ofConfined())
            {
                MemorySegment address = getProcAddr.apply(arena.allocateFrom(prependAPIName("vk", name)));
                return address.equals(NULL) ? Optional.empty() : Optional.of(address);
            }
        };
    }

    @Override
    protected FunctionDescriptor resolveFunctionDescriptor(Method method)
    {
        FunctionDescriptor descriptor = super.resolveFunctionDescriptor(method);
        if (!method.isAnnotationPresent(IndirectCommand.class))
        {
            descriptor = descriptor.insertArgumentLayouts(0, ADDRESS);
        }

        return descriptor;
    }

    @Override
    public MethodHandle dispatch(Method method)
    {
        if (method.getName().equals("get") && method.getReturnType().equals(MemorySegment.class) && method.getParameterCount() == 0)
        {
            return MethodHandles.constant(MemorySegment.class, this.handle);
        }

        MethodHandle methodHandle = super.dispatch(method);
        if (!method.isAnnotationPresent(IndirectCommand.class))
        {
            methodHandle = methodHandle.bindTo(this.handle);
        }

        return methodHandle;
    }
}
