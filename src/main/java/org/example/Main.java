package org.example;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

public class Main {

    static GraphPrinter gp = new GraphPrinter("simple");


    public static void main(String[] args) throws Exception {
        Graph addGraph = new Graph();

//        gp.addln("1 -> 2");

        Pattern pattern = Pattern.compile(".*");
        ArrayList<String> yamlFiles = getResourcesFromClasspath(pattern);

        Yaml yaml = new Yaml();

        for(int i=0;i<yamlFiles.size();i++) {

            Map<String, Object> data = yaml.load(yamlFiles.get(i));

            if (data.containsKey("graph")) {
                Map<String, Object> graph = (Map<String, Object>) data.get("graph");

                for (Map.Entry<String, Object> entry : graph.entrySet()) {
//                    String ruleName = entry.getKey();
                    Map<String, Object> ruleDetails = (Map<String, Object>) entry.getValue();

                    ArrayList<String>inputValues = new ArrayList<>();
                    ArrayList<String> outputValues = new ArrayList<>();

                    if (ruleDetails.containsKey("input-streams")) {
                        Map<String, String> inputStreams = (Map<String, String>) ruleDetails.get("input-streams");
                        for (Map.Entry<String, String> inputStreamEntry : inputStreams.entrySet()) {
                            String inputStreamValue = inputStreamEntry.getValue();
                            inputValues.add(inputStreamValue);
                        }
                    }

                    if (ruleDetails.containsKey("output-streams")) {
                        Map<String, String> outputStreams = (Map<String, String>) ruleDetails.get("output-streams");

                        for (Map.Entry<String, String> outputStreamEntry : outputStreams.entrySet()) {
                            String outputStreamValue = outputStreamEntry.getValue();
                            outputValues.add(outputStreamValue);
                        }
                    }

//                    System.out.println("Rule Name: " + ruleName);
                    for (int j=0;j<inputValues.size();j++){
                        for (int k=0;k<outputValues.size();k++){
                            addGraph.addEdge(formatData(inputValues.get(j)) , formatData(outputValues.get(k)));
                        }
                    }

                }
            }
        }

        addGraph.printGraph();
        gp.print();

    }

    private static ArrayList<String> getResourcesFromClasspath(Pattern pattern) throws Exception {
        String classPath = System.getProperty("java.class.path", ".");

        System.out.println(classPath);
        String[] classPathElements = classPath.split(System.getProperty("path.separator"));
        ArrayList<String> resourceList = new ArrayList<>();


        for (String element : classPathElements) {
            if (element.toLowerCase().endsWith(".jar")) {
                resourceList.addAll(getYamlFilesFromJar(element, pattern));
            }
        }

        return resourceList;
    }

    private static ArrayList<String> getYamlFilesFromJar(String jarFilePath, Pattern pattern) throws Exception {
        ArrayList<String> yamlContents = new ArrayList<>();
        try (ZipFile zipFile = new ZipFile(jarFilePath)) {
            zipFile.stream()
                    .filter(entry -> !entry.isDirectory() && pattern.matcher(entry.getName()).matches() && entry.getName().toLowerCase().endsWith(".yaml"))
                    .forEach(entry -> {
                        if(entry!=null && !entry.getName().endsWith("deploy.yaml"))
                            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                                String yamlContent = readYamlContent(inputStream);
                                yamlContents.add(yamlContent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    });
        }
        return yamlContents;
    }

    private static String readYamlContent(InputStream inputStream) throws Exception {
        StringBuilder contentBuilder = new StringBuilder();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            contentBuilder.append(new String(buffer, 0, bytesRead));
        }
        return contentBuilder.toString();
    }

    public static String formatData(String s){
        if(s.endsWith("$suffix")) {
            s = s.replace("$suffix", "");
            s = s.substring(0, s.length() - 1);;
        }

        return s;
    }

}


