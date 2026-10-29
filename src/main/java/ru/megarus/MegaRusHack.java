package ru.megarus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MegaRusHack implements ClientModInitializer {
    private static final List<Module> MODULES = new ArrayList<>();
    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        registerModules();

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.megarushack.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen instanceof ClickGui) {
                    client.setScreen(null);
                } else if (client.currentScreen == null) {
                    client.setScreen(new ClickGui());
                }
            }

            for (Module module : MODULES) {
                if (module.enabled()) {
                    module.onTick(client);
                }
            }
        });
    }

    private static void registerModules() {
        MODULES.clear();
        MODULES.add(new Module("Координаты", "Демонстрационный HUD-модуль"));
        MODULES.add(new Module("Информация", "Базовая информация о клиенте"));
        MODULES.add(new Module("Удобное меню", "Настройки интерфейса"));
    }

    public static List<Module> modules() {
        return Collections.unmodifiableList(MODULES);
    }

    public static final class Module {
        private final String name;
        private final String description;
        private boolean enabled;

        public Module(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String name() { return name; }
        public String description() { return description; }
        public boolean enabled() { return enabled; }
        public void toggle() { enabled = !enabled; }

        private void onTick(MinecraftClient client) {
            // Безопасный каркас: конкретная логика модулей добавляется отдельно.
        }
    }
}
