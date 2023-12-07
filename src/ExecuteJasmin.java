import java.io.*;

public class ExecuteJasmin {
    public static void main(String[] args) {
        final String execJasminCmd = "java -jar ./jasmin-2.4/jasmin.jar Test.j";

        try {
            Process p = Runtime.getRuntime().exec(execJasminCmd);
            p.waitFor(); // 수행 끝날 때까지 대기

            File testClass = new File("Test.class");
            if (testClass.exists()) {
                String execJavaCmd = "java Test";
                Process process = Runtime.getRuntime().exec(execJavaCmd);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                StringBuilder result = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                    result.append('\n');
                }

                System.out.println("결과: " + result);
            } else {
               testClass.createNewFile();
            }
        } catch (IOException | InterruptedException ie) {
            ie.printStackTrace();
        }

    }
}
