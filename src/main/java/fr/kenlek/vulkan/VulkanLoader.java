package fr.kenlek.vulkan;

import module fr.kenlek.jpgen.api;
import module java.base;

import static fr.kenlek.jpgen.api.ForeignUtils.*;
import static java.lang.foreign.SymbolLookup.libraryLookup;

public final class VulkanLoader
{private VulkanLoader() {}

    public static final UpcallDispatcher UPCALL_DISPATCHER = new LinkingUpcallDispatcher(UpcallTransformer.BOOL32_TRANSFORMER);
    public static final String SYSTEM_PROPERTY_KEY = "vulkan.path";

    private static SymbolLookup loadLookup(Arena arena, String propertyKey, List<LibraryOption> fallbacks)
    {
        SymbolLookup lookup = Optional.ofNullable(System.getProperty(propertyKey))
            .map(path -> libraryLookup(Path.of(path), arena))
            .orElseGet(() -> loadFirstLookup(arena, fallbacks));
        return name -> name.isEmpty() ? Optional.empty() : lookup.find(prependAPIName("vk", name));
    }

    public static Vulkan load(Arena arena, String propertyKey)
    {
        // directly translated from volk
        SymbolLookup lookup = loadLookup(arena, propertyKey, Host.selectLazily(
            new Host.Provider<>(Platform.OS.WINDOWS, () -> List.of(LibraryOption.of("vulkan-1.dll"))),
            new Host.Provider<>(Platform.OS.MACOS, () ->
            {
                List<LibraryOption> values = new ArrayList<>();
                values.add(LibraryOption.of("libvulkan.dylib"));
                values.add(LibraryOption.of("libvulkan.1.dylib"));
                if (System.getenv("DYLD_FALLBACK_LIBRARY_PATH") != null)
                {
                    values.add(LibraryOption.of(Path.of("/usr/local/lib/libvulkan.dylib")));
                }

                values.add(LibraryOption.of("libMoltenVK.dylib"));
                values.add(LibraryOption.of("vulkan.framework/vulkan"));
                values.add(LibraryOption.of("MoltenVK.framework/MoltenVK"));

                return values;
            }),
            new Host.Provider<>(Host.ALL_TARGETS, () -> List.of(
                LibraryOption.of("libvulkan.so.1"),
                LibraryOption.of("libvulkan.so")
            ))
        ));
        return NativeProxies.make(MethodHandles.lookup(), Vulkan.class, new LinkingDowncallDispatcher(lookup));
    }

    public static Vulkan load(Arena arena)
    {
        return load(arena, SYSTEM_PROPERTY_KEY);
    }
}
