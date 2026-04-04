package fr.kenlek.vulkan;

import module fr.kenlek.jpgen.api;
import module java.base;

import fr.kenlek.jpgen.api.data.Buffer;

import static fr.kenlek.jpgen.api.ForeignUtils.makeStructLayout;
import static java.lang.foreign.ValueLayout.*;

@Layout.Container("LAYOUT")
public record VkXlibSurfaceCreateInfoKHR(MemorySegment pointer) implements Addressable
{
    public static final StructLayout LAYOUT = makeStructLayout(
        JAVA_INT.withName("sType"),
        ADDRESS.withName("pNext"),
        JAVA_INT.withName("flags"),
        ADDRESS.withName("dpy"),
        CLong.LAYOUT.withName("window")
    ).withName("VkXlibSurfaceCreateInfoKHR");
    public static final long OFFSET_sType = LAYOUT.byteOffset(PathElement.groupElement("sType"));
    public static final long OFFSET_pNext = LAYOUT.byteOffset(PathElement.groupElement("pNext"));
    public static final long OFFSET_flags = LAYOUT.byteOffset(PathElement.groupElement("flags"));
    public static final long OFFSET_dpy = LAYOUT.byteOffset(PathElement.groupElement("dpy"));
    public static final long OFFSET_window = LAYOUT.byteOffset(PathElement.groupElement("window"));

    public VkXlibSurfaceCreateInfoKHR
    {
        Addressable.checkLayoutConstraints(pointer, LAYOUT);
    }

    public VkXlibSurfaceCreateInfoKHR(SegmentAllocator allocator)
    {
        this(allocator.allocate(LAYOUT));
    }

    public static Buffer<VkXlibSurfaceCreateInfoKHR> buffer(MemorySegment data)
    {
        return Buffer.slices(data, LAYOUT, VkXlibSurfaceCreateInfoKHR::new);
    }

    public static Buffer<VkXlibSurfaceCreateInfoKHR> buffer(SegmentAllocator allocator, long size)
    {
        return Buffer.slices(allocator, LAYOUT, size, VkXlibSurfaceCreateInfoKHR::new);
    }

    @Override
    public StructLayout layout()
    {
        return LAYOUT;
    }

    public void copyFrom(VkXlibSurfaceCreateInfoKHR value)
    {
        MemorySegment.copy(value.pointer(), 0, this.pointer(), 0, LAYOUT.byteSize());
    }

    public int sType()
    {
        return this.pointer().get(JAVA_INT, OFFSET_sType);
    }

    public void sType(int value)
    {
        this.pointer().set(JAVA_INT, OFFSET_sType, value);
    }

    public MemorySegment $sType()
    {
        return this.pointer().asSlice(OFFSET_sType, JAVA_INT);
    }

    public MemorySegment pNext()
    {
        return this.pointer().get(ADDRESS, OFFSET_pNext);
    }

    public void pNext(MemorySegment value)
    {
        this.pointer().set(ADDRESS, OFFSET_pNext, value);
    }

    public MemorySegment $pNext()
    {
        return this.pointer().asSlice(OFFSET_pNext, ADDRESS);
    }

    public int flags()
    {
        return this.pointer().get(JAVA_INT, OFFSET_flags);
    }

    public void flags(int value)
    {
        this.pointer().set(JAVA_INT, OFFSET_flags, value);
    }

    public MemorySegment $flags()
    {
        return this.pointer().asSlice(OFFSET_flags, JAVA_INT);
    }

    public MemorySegment dpy()
    {
        return this.pointer().get(ADDRESS, OFFSET_dpy);
    }

    public void dpy(MemorySegment value)
    {
        this.pointer().set(ADDRESS, OFFSET_dpy, value);
    }

    public MemorySegment $dpy()
    {
        return this.pointer().asSlice(OFFSET_dpy, ADDRESS);
    }

    public MemorySegment $window()
    {
        return this.pointer().asSlice(OFFSET_window, CLong.LAYOUT);
    }

    public CLong window()
    {
        return new CLong(this.$window());
    }

    public void window(CLong value)
    {
        value.unwrap(this.$window());
    }
}
