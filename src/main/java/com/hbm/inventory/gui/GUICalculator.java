package com.hbm.inventory.gui;

import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Locale;
import java.util.Objects;
import java.util.Stack;


public class GUICalculator extends Screen {
    private static final ResourceLocation texture = ResLocation.ResLocation(RefStrings.MODID, "textures/gui/calculator.png");
    private final int xSize = 220;
    private final int ySize = 50;
    private EditBox inputField; // Изменено с GuiTextField
    private String latestResult = "?";

    // Конструктор теперь принимает Component
    public GUICalculator() {
        super(Component.literal("Calculator"));
    }

    @Override
    protected void init() {
        super.init();

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        // Создаем EditBox вместо GuiTextField
        this.inputField = new EditBox(this.font,
                x + 5, y + 8,
                210, 13,
                Component.literal(""));
        this.inputField.setTextColor(-1);
        this.inputField.setBordered(false); // Убираем рамку, т.к. рисуем свою
        this.inputField.setMaxLength(1000);
        this.inputField.setFocused(true);
        this.inputField.setValue(""); // Устанавливаем значение вместо setText

        // Добавляем поле на экран
        this.addRenderableWidget(this.inputField);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Обрабатываем Enter
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            String input = this.inputField.getValue()
                    .replaceAll("[^\\d+\\-*/^!.()\\sA-Za-z]+", "");

            if (!input.isEmpty()) {
                try {
                    double result = evaluateExpression(input);
                    String plainStringRepresentation =
                            (new BigDecimal(result, MathContext.DECIMAL64)).toPlainString();

                    // Копируем в буфер обмена
                    Objects.requireNonNull(this.minecraft).keyboardHandler.setClipboard(plainStringRepresentation);

                    // Устанавливаем результат
                    this.inputField.setValue(plainStringRepresentation);
                    this.inputField.setCursorPosition(plainStringRepresentation.length());
                    this.inputField.setHighlightPos(0);

                } catch (Exception ignored) {
                    this.latestResult = "Error";
                }
            }
            return true;
        }

        // Передаем обработку ввода полю
        if (this.inputField.keyPressed(keyCode, scanCode, modifiers)) {
            updateResult();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.inputField.charTyped(codePoint, modifiers)) {
            updateResult();
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Рендерим фон (если нужно)
        this.renderBackground(guiGraphics);

        // Рисуем текстуру калькулятора
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        guiGraphics.blit(texture, x, y, 0, 0, xSize, ySize);

        // Рендерим поле ввода
        this.inputField.render(guiGraphics, mouseX, mouseY, partialTicks);

        // Рисуем результат
        guiGraphics.drawString(this.font, "=" + latestResult,
                x + 5, y + 30, -1, false);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    // Метод для правильного фона (опционально)
    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics) {
        if (Objects.requireNonNull(this.minecraft).level != null) {
            // Полупрозрачный черный фон
            guiGraphics.fillGradient(0, 0, this.width, this.height,
                    -1072689136, -804253680);
        } else {
            super.renderBackground(guiGraphics);
        }
    }

    @Override
    public void resize(net.minecraft.client.@NotNull Minecraft mc, int width, int height) {
        super.resize(mc, width, height);
        // Позиция поля пересчитается автоматически
    }

    // Обновление результата в реальном времени
    private void updateResult() {
        String input = this.inputField.getValue()
                .replaceAll("[^\\d+\\-*/^!.()\\sA-Za-z]+", "");

        if (input.isEmpty()) {
            this.latestResult = "?";
            return;
        }

        try {
            this.latestResult = Double.toString(evaluateExpression(input));
        } catch (Exception e) {
            this.latestResult = "Error";
        }
    }



    /**
     * Mathematically evaluates user-inputted strings<br>
     * It is recommended to catch all exceptions when using this
     */
    public static double evaluateExpression(String input) {
        if (input.contains("^")) input = preEvaluatePower(input);

        char[] tokens = input.toCharArray();
        Stack<Double> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == ' ') continue;

            if (tokens[i] >= '0' && tokens[i] <= '9' || tokens[i] == '.' || (tokens[i] == '-' && (i == 0 || "+-*/^(".contains(String.valueOf(tokens[i - 1]))))) {
                StringBuilder buffer = new StringBuilder();
                if (tokens[i] == '-') {
                    buffer.append('-'); // for negative numbers
                    i++;
                }
                while (i < tokens.length && (tokens[i] >= '0' && tokens[i] <= '9' || tokens[i] == '.')) buffer.append(tokens[i++]);
                values.push(Double.parseDouble(buffer.toString()));
                i--;
            } else if (tokens[i] == '(') operators.push(Character.toString(tokens[i]));
            else if (tokens[i] == ')') {
                while (!operators.isEmpty() && operators.peek().charAt(0) != '(')
                    values.push(evaluateOperator(operators.pop().charAt(0), values.pop(), values.pop()));
                operators.pop();
                if (!operators.isEmpty() && operators.peek().length() > 1)
                    values.push(evaluateFunction(operators.pop(), values.pop()));
            } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/' || tokens[i] == '^') {
                while (!operators.isEmpty() && hasPrecedence(String.valueOf(tokens[i]), operators.peek()))
                    values.push(evaluateOperator(operators.pop().charAt(0), values.pop(), values.pop()));
                operators.push(Character.toString(tokens[i]));
            } else if (tokens[i] == '!') {
                values.push((double) factorial((int) Math.round(values.pop())));
            }else if (tokens[i] >= 'A' && tokens[i] <= 'Z' || tokens[i] >= 'a' && tokens[i] <= 'z') {
                StringBuilder charBuffer = new StringBuilder();
                while (i < tokens.length && (tokens[i] >= 'A' && tokens[i] <= 'Z' || tokens[i] >= 'a' && tokens[i] <= 'z'))
                    charBuffer.append(tokens[i++]);
                String string = charBuffer.toString();
                if (string.equalsIgnoreCase("pi")) values.push(Math.PI);
                else if (string.equalsIgnoreCase("e")) values.push(Math.E);
                else operators.push(string.toLowerCase(Locale.ROOT));
                i--;
            }
        }

        // if the expression is correctly formatted, no function is remaining
        while (!operators.empty()) values.push(evaluateOperator(operators.pop().charAt(0), values.pop(), values.pop()));

        return values.pop();
    }

    private static double evaluateOperator(char operator, double x, double y) {
        return switch (operator) {
            case '+' -> y + x;
            case '-' -> y - x;
            case '*' -> y * x;
            case '/' -> y / x;
            case '^' -> Math.pow(y, x); // should not happen here, but oh well
            default -> 0;
        };
    }

    private static double evaluateFunction(String function, double x) {
        return switch (function) {
            case "sqrt" -> Math.sqrt(x);
            case "sin" -> Math.sin(x);
            case "cos" -> Math.cos(x);
            case "tan" -> Math.tan(x);
            case "asin" -> Math.asin(x);
            case "acos" -> Math.acos(x);
            case "atan" -> Math.atan(x);
            case "log" -> Math.log10(x);
            case "ln" -> Math.log(x);
            case "ceil" -> Math.ceil(x);
            case "floor" -> Math.floor(x);
            case "round" -> Math.round(x);
            default -> 0;
        };
    }

    /** Returns whether {@code second} has precedence over {@code first} */
    private static boolean hasPrecedence(String first, String second) {
        if (second.length() > 1) return false;

        char firstChar = first.charAt(0);
        char secondChar = second.charAt(0);

        if (secondChar == '(' || secondChar == ')') return false;
        else return (firstChar != '*' && firstChar != '/' && firstChar != '^') || (secondChar != '+' && secondChar != '-');
    }

    /** Returns the input with all powers evaluated */
    private static String preEvaluatePower(String input) {
        do {
            int powerOperatorIndex = input.lastIndexOf('^');

            // find base
            boolean previousTokenIsParentheses = input.charAt(powerOperatorIndex - 1) == ')';
            int parenthesesDepth = previousTokenIsParentheses ? 1 : 0;
            int baseExpressionStart = previousTokenIsParentheses ? powerOperatorIndex - 2 : powerOperatorIndex - 1;
            baseLoop:
            for (; baseExpressionStart >= 0; baseExpressionStart--) { // search backwards
                switch (input.charAt(baseExpressionStart)) {
                    case ')':
                        if (previousTokenIsParentheses) parenthesesDepth++;
                        else break baseLoop;
                        break;
                    case '(':
                        if (previousTokenIsParentheses && parenthesesDepth > 0) parenthesesDepth--;
                        else break baseLoop;
                        break;
                    case '+': case '-': case '*': case '/': case '^':
                        if (parenthesesDepth == 0) break baseLoop;
                }
            }
            baseExpressionStart++; // go one token forward again
            if (parenthesesDepth > 0) throw new IllegalArgumentException("Incomplete parentheses");

            // find exponent
            boolean nextTokenIsParentheses = input.charAt(powerOperatorIndex + 1) == '(';
            parenthesesDepth = nextTokenIsParentheses ? 1 : 0;
            int exponentExpressionEnd = nextTokenIsParentheses ? powerOperatorIndex + 2 : powerOperatorIndex + 1;
            exponentLoop:
            for (; exponentExpressionEnd < input.length(); exponentExpressionEnd++) {
                switch (input.charAt(exponentExpressionEnd)) {
                    case '(':
                        if (nextTokenIsParentheses) parenthesesDepth++;
                        else break exponentLoop;
                        break;
                    case ')':
                        if (nextTokenIsParentheses && parenthesesDepth > 0) parenthesesDepth--;
                        else break exponentLoop;
                        break;
                    case '+': case '-': case '*': case '/': case '^':
                        if (parenthesesDepth == 0) break exponentLoop;
                }
            }
            if (parenthesesDepth > 0) throw new IllegalArgumentException("Incomplete parentheses");

            double base = evaluateExpression(input.substring(baseExpressionStart, powerOperatorIndex));
            double exponent = evaluateExpression(input.substring(powerOperatorIndex + 1, exponentExpressionEnd));
            double result = Math.pow(base, exponent);
            // use big decimal to avoid scientific notation messing with the calculation
            input = input.substring(0, baseExpressionStart) + (new BigDecimal(result, MathContext.DECIMAL64)).toPlainString() + input.substring(exponentExpressionEnd);
        } while (input.contains("^"));

        return input;
    }

    private static int factorial(int in) {
        if (in < 0) throw new IllegalArgumentException("Factorial needs n >= 0");
        if (in < 2) return 1;
        int p = 1, r = 1;
        factorialCurrentN = 1;
        int h = 0, shift = 0, high = 1;
        int log2n = log2(in);
        while (h != in) {
            shift += h;
            h = in >> log2n--;
            int len = high;
            high = (h - 1) | 1;
            len = (high - len) / 2;

            if (len > 0) {
                p *= factorialProduct(len);
                r *= p;
            }
        }

        return r << shift;
    }

    private static int factorialCurrentN;

    private static int factorialProduct(int in) {
        int m = in / 2;
        if (m == 0) return factorialCurrentN += 2;
        if (in == 2) return (factorialCurrentN += 2) * (factorialCurrentN += 2);
        return factorialProduct(in - m) * factorialProduct(m);
    }

    private static int log2(int in) {
        int log = 0;
        if((in & 0xffff0000) != 0) { in >>>= 16; log = 16; }
        if(in >= 256) { in >>>= 8; log += 8; }
        if(in >= 16) { in >>>= 4; log += 4; }
        if(in >= 4) { in >>>= 2; log += 2; }
        return log + (in >>> 1);
    }
}
