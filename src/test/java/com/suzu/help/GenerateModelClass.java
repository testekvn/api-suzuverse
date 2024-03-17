package com.suzu.help;

import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.*;
import org.jsonschema2pojo.rules.RuleFactory;

import java.io.*;
import java.net.URL;
import java.util.Base64;

public class GenerateModelClass {
    public static void main(String[] args) {
        GenerateModelClass generateModel = new GenerateModelClass();
        try {
            generateModel.convertJsonToJavaClass("Tesst");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateModel() {
        try {
            File f = new File("src/test/java/com/testek/help/modelData.txt");

            FileReader fileReader = new FileReader(f);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            StringBuilder classContent = new StringBuilder();
            StringBuilder constructorContent = new StringBuilder();
            boolean isClass = false;
            String fileName = "";

            classContent.append("import com.testek.common.BaseModel;\n" +
                    "import com.misa.helpers.DataModel;\n" +
                    "import lombok.Data;\n\n" +
                    "@Data\n");

            while ((line = bufferedReader.readLine()) != null) {
                if (!isClass) {
                    classContent.append("public class " + line.trim() + " extends BaseModel {\n");
                    constructorContent.append("public " + line.trim() + "(){\nsuper();\n");
                    isClass = true;
                    fileName = line;
                } else {
                    String first = line.substring(0, 1);
                    String variable = (first.toLowerCase() + line.substring(1)).trim();
                    classContent.append(" public DataModel " + first.toLowerCase() + line.substring(1) + ";\n");
                    constructorContent.append(String.format("%s = createDataModelObj(\"%s\", \"%s\");\n", variable, line.trim(), line.trim()));
                }
            }

            constructorContent.append("}\n");
            classContent.append(constructorContent);
            classContent.append("\n}");

            fileReader.close();
            bufferedReader.close();


            FileWriter fileWriter = new FileWriter(String.format("src/test/java/com/testek/help/%s.java", fileName));
            fileWriter.write(classContent.toString());
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            //Log.info(e.getMessage());
        }
    }

    public void decodeBase64() {
        String data =
                "W3sicHJvcGVydHkiOiJpc19zZWxlY3RlZCIsImRlc2MiOnRydWV9LHsicHJvcGVydHkiOiJwb3N0ZWRfZGF0ZSIsImRlc2MiOnRydWV9LHsicHJvcGVydHkiOiJyZWZkYXRlIiwiZGVzYyI6dHJ1ZX0seyJwcm9wZXJ0eSI6InJlZm5vX2ZpbmFuY2UiLCJkZXNjIjp0cnVlfSx7InByb3BlcnR5IjoicmVmbm9fbWFuYWdlbWVudCIsImRlc2MiOnRydWV9LHsicHJvcGVydHkiOiJzb3J0X29yZGVyIiwiZGVzYyI6ZmFsc2V9XQ%3D%3D";
        byte[] value = Base64.getDecoder().decode(data);
        System.out.println("Vincent => " + value.toString());
    }

    public void convertJsonToJavaClass(String javaClassName)
            throws IOException {
        String outFolderPath = System.getProperty("user.dir") + "/src/test/java/com/suzu/help/result";
        File outputJavaClassDirectory = new File(outFolderPath);
        if (!outputJavaClassDirectory.exists()) outputJavaClassDirectory.mkdirs();
        String packageName = "";
        String srcFilePath = System.getProperty("user.dir") + "/src/test/java/com/suzu/help/jsonData.json";

        URL inputJsonUrl = new File(srcFilePath).toURI().toURL();
        JCodeModel jcodeModel = new JCodeModel();
        GenerationConfig config = new DefaultGenerationConfig() {
            @Override
            public boolean isGenerateBuilders() {
                return false;
            }

            @Override
            public SourceType getSourceType() {
                return SourceType.JSON;
            }

            @Override
            public boolean isIncludeDynamicSetters() {
                return false;
            }

            @Override
            public boolean isIncludeSetters() {
                return false;
            }

            @Override
            public SourceSortOrder getSourceSortOrder() {
                return null;
            }

            @Override
            public boolean isIncludeGetters() {
                return false;
            }

            @Override
            public boolean isIncludeDynamicBuilders() {
                return false;
            }

            @Override
            public boolean isIncludeAdditionalProperties() {
                return false;
            }
        };

        SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()), new SchemaGenerator());
        mapper.generate(jcodeModel, javaClassName, packageName, inputJsonUrl);

        jcodeModel.build(outputJavaClassDirectory);
    }
}
