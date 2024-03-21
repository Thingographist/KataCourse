package KataWelcome;

import java.util.regex.Pattern;

class Calculator {

    private static final class Expr {
        Long left;
        String op;
        Long right;

        Expr(Long left, String op, Long right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }

        public Long calc() throws Exception {
            switch (op) {
                case "+":
                    return left + right;
                case "-":
                    return left - right;
                case "*":
                    return left * right;
                case "/":
                    return left / right;
            }
            throw new Exception("Неизвестный оператор");
        }
    }

    private interface Interpreter {
        public Expr read(String input);
        public String print(Long value);
        public Boolean isValid(Expr expr);
    }
    
    private static final class ArabInterpreter implements Interpreter {
        private final Pattern arabicExpr = Pattern.compile("^(\\d+)\\s*([-/*+])\\s*(\\d+)$");
        public Expr read(String input) {
            var expr = arabicExpr.matcher(input);
            if (expr.find()) {
                var left = Long.parseLong(expr.group(1));
                var op = expr.group(2);
                var right = Long.parseLong(expr.group(3));
                return new Expr(left, op, right);
            }
            return null;
        }

        public String print(Long value) {
            return Long.toString(value);
        }

        public Boolean isValid(Expr expr) {
            return true;
        }
    }
    
    private static final class RomeInterpreter implements Interpreter {
        private final Pattern romeExpr = Pattern.compile("^([IVXCML]+)\\s*([-/*+])\\s*([IVXCML]+)$");
        
        private Long praseRome(String input) {
            var arr = input.toCharArray();
            long result = 0;
            char prev = 0;
            for (int i = arr.length - 1; i >= 0; i--) {
                switch (arr[i]) {
                    case 'I':
                        result += ("XV".indexOf(prev) > -1) ? -1 : 1;
                        break;
                    case 'V':
                        result += 5;
                        break;
                    case 'X':
                        result += ("CL".indexOf(prev) > -1) ? -10 : 10;
                        break;
                    case 'L':
                        result += 50;
                        break;
                    case 'C':
                        result += ('M' == prev) ? -100 : 100;
                        break;
                    case 'M':
                        result += 1000;
                        break;
                    default:
                        break;
                }
                prev = arr[i];
            }
            return result;
        }

        public Expr read(String input) {
            var expr = romeExpr.matcher(input);
            if (expr.find()) {
                var left = praseRome(expr.group(1));
                var op = expr.group(2);
                var right = praseRome(expr.group(3));
                return new Expr(left, op, right);
            }
            return null;
        }

        public String print(Long value) {
            Long rem = value;
            String result = "";
            if (rem / 1000 > 0) {
                for (int i = 0; i < rem / 1000; i++) {
                    result += "M";
                }
                rem %= 1000;
            }
            if (rem >= 900) {
                result += "CM";
                rem -= 900;
            }
            if (rem / 100 > 0) {
                for (int i = 0; i < rem / 100; i++) {
                    result += "C";
                }
                rem %= 100;
            }
            if (rem >= 90) {
                result += "XC";
                rem -= 90;
            }
            if (rem / 50 > 0) {
                result += "L";
                rem %= 50;
            }
            if (rem >= 40) {
                result += "XL";
                rem -= 40;
            }
            if (rem / 10 > 0) {
                for (int i = 0; i < rem / 10; i++) {
                    result += "X";
                }
                rem %= 10;
            }
            if (rem >= 9) {
                result += "IX";
                rem -= 9;
            }
            if (rem / 5 > 0) {
                result += "V";
                rem %= 5;
            }
            if (rem >= 4) {
                result += "IV";
                rem -= 4;
            }
            if (rem > 0) {
                for (int i = 0; i < rem; i++) {
                    result += "I";
                }
            }
            return result;
        }

        public Boolean isValid(Expr expr) {
            if (!expr.op.equals("-")) return true;
            return expr.left > expr.right;
        }
    }

    public static String calc(String input) throws Exception {
        Interpreter[] interpreters = { new RomeInterpreter(), new ArabInterpreter() };
        for (Interpreter interpreter : interpreters) {
            var expr = interpreter.read(input);
            if (expr == null) continue;
            if (!interpreter.isValid(expr)) {
                continue;
            }
            return interpreter.print(expr.calc());
        }
        throw new Exception("Ошибка в выражении");
    }

    // public static void main(String[] args) throws Exception{
    //     String[] variants = {
    //         "1 + 2", // 3 
    //         "VI / III", // II
    //         "I - II", // throws  Exception : в римской системе нет отрицательных чисел
    //         "I + 1", // throws Exception : используются одновременно разные системы счисления
    //         "1", //  throws Exception : строка не является математической операцией
    //         "1 + 2 + 3" // throws Exception : формат математической операции не удовлетворяет заданию - два операнда и один оператор (+, -, /, *)
    //     };

    //     for (String s : variants) {
    //         try {
    //             System.out.println(calc(s));
    //         } catch (Exception e) {
    //             System.out.println("Exception: " + e.getMessage());
    //         }
    //     }
    // }
}