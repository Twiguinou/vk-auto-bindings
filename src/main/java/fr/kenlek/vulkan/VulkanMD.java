package fr.kenlek.vulkan;

import module fr.kenlek.jpgen.api;
import module java.base;

@Layout.Generic({
    @Layout.Case(target = boolean.class, layout = @Layout(value = "JAVA_INT", container = ValueLayout.class))
})
public interface VulkanMD
{
    static VulkanMD load(VkInstance instance)
    {
        return NativeProxies.make(
            MethodHandles.lookup(), VulkanMD.class,
            new HandleDowncallDispatcher(instance.get(), HandleDowncallDispatcher.symbolLookup(instance::getInstanceProcAddr))
        );
    }

    int createXlibSurfaceKHR(MemorySegment pCreateInfo, MemorySegment pAllocator, MemorySegment pSurface);

    @IndirectCommand
    boolean getPhysicalDeviceXlibPresentationSupportKHR(MemorySegment physicalDevice, int queueFamilyIndex, MemorySegment dpy, CLong visualID);

    int createWin32SurfaceKHR(MemorySegment pCreateInfo, MemorySegment pAllocator, MemorySegment pSurface);

    @IndirectCommand
    boolean getPhysicalDeviceWin32PresentationSupportKHR(MemorySegment physicalDevice, int queueFamilyIndex);

    int createMetalSurfaceEXT(MemorySegment pCreateInfo, MemorySegment pAllocator, MemorySegment pSurface);
}
