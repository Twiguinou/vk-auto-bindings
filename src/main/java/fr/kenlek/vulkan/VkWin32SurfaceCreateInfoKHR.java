package fr.kenlek.vulkan;

import module fr.kenlek.jpgen.api;
import module java.base;

import fr.kenlek.jpgen.api.data.Buffer;

import static fr.kenlek.jpgen.api.ForeignUtils.makeStructLayout;
import static java.lang.foreign.ValueLayout.*;

@Layout.Container("LAYOUT")
public record VkWin32SurfaceCreateInfoKHR(MemorySegment pointer) implements Addressable
{
    public static final StructLayout LAYOUT = makeStructLayout(
        JAVA_INT.withName("sType"),
        ADDRESS.withName("pNext"),
        JAVA_INT.withName("flags"),
        ADDRESS.withName("hinstance"),
        ADDRESS.withName("hwnd")
    ).withName("VkWin32SurfaceCreateInfoKHR");
    public static final long OFFSET_sType = LAYOUT.byteOffset(PathElement.groupElement("sType"));
    public static final long OFFSET_pNext = LAYOUT.byteOffset(PathElement.groupElement("pNext"));
    public static final long OFFSET_flags = LAYOUT.byteOffset(PathElement.groupElement("flags"));
    public static final long OFFSET_hinstance = LAYOUT.byteOffset(PathElement.groupElement("hinstance"));
    public static final long OFFSET_hwnd = LAYOUT.byteOffset(PathElement.groupElement("hwnd"));

    public VkWin32SurfaceCreateInfoKHR
    {
        Addressable.checkLayoutConstraints(pointer, LAYOUT);
    }

    public VkWin32SurfaceCreateInfoKHR(SegmentAllocator allocator)
    {
        this(allocator.allocate(LAYOUT));
    }

    public static Buffer<VkWin32SurfaceCreateInfoKHR> buffer(MemorySegment data)
    {
        return Buffer.slices(data, LAYOUT, VkWin32SurfaceCreateInfoKHR::new);
    }

    public static Buffer<VkWin32SurfaceCreateInfoKHR> buffer(SegmentAllocator allocator, long size)
    {
        return Buffer.slices(allocator, LAYOUT, size, VkWin32SurfaceCreateInfoKHR::new);
    }

    @Override
    public StructLayout layout()
    {
        return LAYOUT;
    }

    public void copyFrom(VkWin32SurfaceCreateInfoKHR value)
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

    public MemorySegment hinstance()
    {
        return this.pointer().get(ADDRESS, OFFSET_hinstance);
    }

    public void hinstance(MemorySegment value)
    {
        this.pointer().set(ADDRESS, OFFSET_hinstance, value);
    }

    public MemorySegment $hinstance()
    {
        return this.pointer().asSlice(OFFSET_hinstance, ADDRESS);
    }

    public MemorySegment hwnd()
    {
        return this.pointer().get(ADDRESS, OFFSET_hwnd);
    }

    public void hwnd(MemorySegment value)
    {
        this.pointer().set(ADDRESS, OFFSET_hwnd, value);
    }

    public MemorySegment $hwnd()
    {
        return this.pointer().asSlice(OFFSET_hwnd, ADDRESS);
    }
}
