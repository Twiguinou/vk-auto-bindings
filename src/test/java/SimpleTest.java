import module java.base;

import fr.kenlek.jpgen.api.data.Buffer;
import fr.kenlek.vulkan.*;

import static fr.kenlek.vulkan.VkResult.VK_SUCCESS;
import static fr.kenlek.vulkan.VkStructureType.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
import static java.lang.foreign.MemorySegment.NULL;

public final class SimpleTest
{private SimpleTest() {}

    static void main()
    {
        Arena arena = Arena.ofAuto();

        Vulkan vulkan = VulkanLoader.load(arena);

        VkApplicationInfo applicationInfo = new VkApplicationInfo(arena);

        VkInstanceCreateInfo instanceCreateInfo = new VkInstanceCreateInfo(arena);
        instanceCreateInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
        instanceCreateInfo.pApplicationInfo(applicationInfo.pointer());

        Buffer<MemorySegment> pInstance = Buffer.addresses(arena, 1);
        if (vulkan.createInstance(instanceCreateInfo.pointer(), NULL, pInstance.pointer()) != VK_SUCCESS)
        {
            throw new RuntimeException("Unable to create instance!");
        }

        VkInstance instance = VkInstance.load(vulkan, pInstance.getFirst());
        IO.println(instance.get());

        instance.destroyInstance(NULL);
    }
}
