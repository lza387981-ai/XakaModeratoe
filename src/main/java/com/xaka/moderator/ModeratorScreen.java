package com.xaka.moderator;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.Locale;

public final class ModeratorScreen extends Screen {
    private TextFieldWidget playerField;
    private TextFieldWidget durationField;
    private String selectedAction = "ban";
    private String selectedReason = "2.6";
    private String status = "Выбери действие и заполни поля";

    public ModeratorScreen() {
        super(Text.literal("XakaModerator"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int top = this.height / 2 - 105;

        playerField = new TextFieldWidget(textRenderer, cx - 135, top + 25, 270, 20,
                Text.literal("Ник игрока"));
        playerField.setMaxLength(16);
        playerField.setPlaceholder(Text.literal("Введите ник игрока"));
        addDrawableChild(playerField);

        durationField = new TextFieldWidget(textRenderer, cx - 135, top + 70, 130, 20,
                Text.literal("Срок"));
        durationField.setMaxLength(12);
        durationField.setText("30d");
        durationField.setPlaceholder(Text.literal("Например: 30d"));
        addDrawableChild(durationField);

        addDrawableChild(ButtonWidget.builder(Text.literal("БАН"), b -> {
            selectedAction = "ban";
            durationField.setText("30d");
            selectedReason = "2.6";
            status = "Бан: выбери срок и причину";
        }).dimensions(cx + 5, top + 70, 65, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("МУТ"), b -> {
            selectedAction = "mute";
            durationField.setText("15m");
            selectedReason = "3.5";
            status = "Мут: выбери срок и причину";
        }).dimensions(cx + 75, top + 70, 60, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Причина 2.6"), b -> {
            selectedReason = "2.6";
            status = "Выбрана причина 2.6";
        }).dimensions(cx - 135, top + 105, 130, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Причина 3.5"), b -> {
            selectedReason = "3.5";
            status = "Выбрана причина 3.5";
        }).dimensions(cx + 5, top + 105, 130, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Причина 3.3"), b -> {
            selectedReason = "3.3";
            status = "Выбрана причина 3.3";
        }).dimensions(cx - 135, top + 132, 130, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("ПОДТВЕРДИТЬ"), b -> submit())
                .dimensions(cx + 5, top + 132, 130, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Закрыть"), b -> close())
                .dimensions(cx - 45, top + 165, 90, 20).build());
    }

    private void submit() {
        String player = playerField.getText().trim();
        String duration = durationField.getText().trim();

        if (!player.matches("[A-Za-z0-9_]{1,16}")) {
            status = "Проверь ник игрока";
            return;
        }
        if (!duration.matches("[0-9]+[smhd]")) {
            status = "Неверный срок. Пример: 15m или 30d";
            return;
        }
        if (selectedAction.equals("ban") && !selectedReason.equals("2.6")) {
            status = "Для бана пока настроена причина 2.6";
            return;
        }
        if (selectedAction.equals("mute") &&
                !(selectedReason.equals("3.5") || selectedReason.equals("3.3"))) {
            status = "Для мута выбери причину 3.5 или 3.3";
            return;
        }

        String command = selectedAction + " " + player + " " + duration + " | " + selectedReason;
        XakaModeratorClient.sendCommand(command);
        status = "Команда отправлена: /" + command;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int cx = this.width / 2;
        int top = this.height / 2 - 105;

        context.fill(cx - 155, top - 12, cx + 155, top + 195, 0xF0181426);
        context.drawBorder(cx - 155, top - 12, 310, 207, 0xFF8B5CF6);
        context.drawCenteredTextWithShadow(textRenderer, "XakaModerator", cx, top - 2, 0xFFD8B4FE);
        context.drawTextWithShadow(textRenderer, "Игрок", cx - 135, top + 10, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, "Срок наказания", cx - 135, top + 55, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, "Действие: " + selectedAction.toUpperCase(Locale.ROOT)
                + "   |   Причина: " + selectedReason, cx - 135, top + 91, 0xFFE9D5FF);
        context.drawCenteredTextWithShadow(textRenderer, status, cx, top + 153, 0xFFFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
