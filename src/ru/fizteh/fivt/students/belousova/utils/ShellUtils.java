package ru.fizteh.fivt.students.belousova.utils;

import ru.fizteh.fivt.students.belousova.shell.Command;
import ru.fizteh.fivt.students.belousova.shell.MainShell;
import ru.fizteh.fivt.students.belousova.shell.ShellState;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

public class ShellUtils {
    public static void batchMode(String[] args, ShellState state) {

        String s = join(Arrays.asList(args), " ");
        try {
            stringHandle(s, state);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static String join(Iterable<?> list, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object si : list) {
            if (!first) {
                sb.append(separator);
            } else {
                first = false;
            }
            sb.append(si);
        }
        return sb.toString();
    }

    public static void interactiveMode(InputStream inputStream, ShellState state) {

        do {
            System.out.print("$ ");
            Scanner scanner = new Scanner(inputStream);
            String s = scanner.nextLine();
            try {
                stringHandle(s, state);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } while (!Thread.currentThread().isInterrupted());
    }

    public static void stringHandle(String s, ShellState state) throws IOException{

        String[] commands = s.trim().split("\\s*;\\s*");

        for (String com : commands) {
            String[] tokens = com.split("\\s+");
            try {
                String commandName = tokens[0];
                if (!MainShell.commandList.containsKey(commandName)) {
                    throw new IOException("Invalid command");
                }

                Command command = MainShell.commandList.get(commandName);
                if (command.getArgCount() + 1 > tokens.length) {
                    throw new IOException("missing file operand");
                }
                if (command.getArgCount() + 1 < tokens.length) {
                    throw new IOException("too many arguments");
                }
                command.execute(tokens);
            } catch (IOException e) {
                throw new IOException(tokens[0] + ": " + e.getMessage(), e);
            }
        }
    }
}
