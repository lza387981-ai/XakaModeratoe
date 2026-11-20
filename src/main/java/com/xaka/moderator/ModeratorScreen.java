package com.xaka.moderator;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class ModeratorScreen extends Screen {
    private TextFieldWidget playerField;
    private TextFieldWidget durationField;
    private String selectedAction = "ban";
    private String selectedReason = "2.6";
    private String status = "Выбери игрока, действие и срок";
    private final List<String> onlineNames = new ArrayList<>();

    public ModeratorScreen() {
        super(Text.literal("XakaModerator"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int left = cx - 205;
        int top = this.height / 2 - 125;

        onlineNames.clear();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() != null) {
            for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
                String name = entry.getProfile().getName();
                if (name != null && name.matches("[A-Za-z0-9_]{1,16}")) {
                    onlineNames.add(name);
                }
            }
        }
        onlineNames.sort(Comparator.naturalOrder());

        playerField = new TextFieldWidget(textRenderer, left + 18, top + 43, 230, 20,
                Text.literal("Ник игрока"));
        playerField.setMaxLength(16);
        playerField.setPlaceholder(Text.literal("Введи ник или выбери справа"));
        addDrawableChild(playerField);

        durationField = new TextFieldWidget(textRenderer, left + 18, top + 91, 105, 20,
                Text.literal("Срок"));
        durationField.setMaxLength(12);
        durationField.setText("30d");
        durationField.setPlaceholder(Text.literal("30d"));
        addDrawableChild(durationField);

        addDrawableChild(ButtonWidget.builder(Text.literal("БАН"), b -> {
            selectedAction = "ban";
            durationField.setText("30d");
            selectedReason = "2.6";
            status = "Бан выбран. Проверь причину и срок.";
        }).dimensions(left + 135, top + 91, 55, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("МУТ"), b -> {
            selectedAction = "mute";
            durationField.setText("15m");
            selectedReason = "3.5";
            status = "Мут выбран. Проверь причину и срок.";
        }).dimensions(left + 197, top + 91, 55, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Причина 2.6"), b -> chooseReason("2.6"))
                .dimensions(left + 18, top + 130, 112, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Причина 3.5"), b -> chooseReason("3.5"))
                .dimensions(left + 138, top + 130, 112, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Причина 3.3"), b -> chooseReason("3.3"))
                .dimensions(left + 18, top + 158, 112, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("ПОДТВЕРДИТЬ"), b -> submit())
                .dimensions(left + 138, top + 158, 112, 20).build());

        int shown = Math.min(6, onlineNames.size());
        for (int i = 0; i < shown; i++) {
            String name = onlineNames.get(i);
            addDrawableChild(ButtonWidget.builder(Text.literal(name), b -> {
                playerField.setText(name);
                status = "Выбран игрок: " + name;
            }).dimensions(left + 275, top + 43 + i * 24, 112, 20).build());
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Закрыть"), b -> close())
                .dimensions(cx - 45, top + 207, 90, 20).build());
    }

    private void chooseReason(String reason) {
        selectedReason = reason;
        status = "Выбрана причина " + reason;
    }

    private void submit() {
        String player = playerField.getText().trim();
        String duration = durationField.getText().trim();

        if (!player.matches("[A-Za-z0-9_]{1,16}")) {
            status = "Проверь ник: только латиница, цифры и _";
            return;
        }
        if (!duration.matches("[0-9]+[smhd]")) {
            status = "Неверный срок. Пример: 15m или 30d";
            return;
        }
        if (selectedAction.equals("ban") && !selectedReason.equals("2.6")) {
            status = "Для бана из известных кодов подходит 2.6";
            return;
        }
        if (selectedAction.equals("mute") &&
                !(selectedReason.equals("3.5") || selectedReason.equals("3.3"))) {
            status = "Для мута выбери причину 3.5 или 3.3";
            return;
        }

        String command = selectedAction + " " + player + " " + duration + " | " + selectedReason;
        XakaModeratorClient.sendCommand(command);
        status = "Отправлена команда: /" + command;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int cx = this.width / 2;
        int left = cx - 205;
        int top = this.height / 2 - 125;

        context.fill(left, top, left + 410, top + 245, 0xF0181426);
        context.drawBorder(left, top, 410, 245, 0xFF8B5CF6);
        context.drawCenteredTextWithShadow(textRenderer, "XakaModerator", cx, top + 12, 0xFFD8B4FE);
        context.drawTextWithShadow(textRenderer, "Ник игрока", left + 18, top + 31, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, "Срок", left + 18, top + 79, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, "Онлайн — нажми на ник", left + 275, top + 27, 0xFFD8B4FE);
        if (onlineNames.isEmpty()) {
            context.drawTextWithShadow(textRenderer, "Нет списка игроков", left + 275, top + 48, 0xFFB8B0C8);
        } else if (onlineNames.size() > 6) {
            context.drawTextWithShadow(textRenderer, "+" + (onlineNames.size() - 6) + " ещё", left + 275, top + 195, 0xFFB8B0C8);
        }
        context.drawTextWithShadow(textRenderer,
                "Действие: " + selectedAction.toUpperCase(Locale.ROOT) + "   Причина: " + selectedReason,
                left + 18, top + 116, 0xFFE9D5FF);
        context.drawCenteredTextWithShadow(textRenderer, status, cx, top + 188, 0xFFFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
