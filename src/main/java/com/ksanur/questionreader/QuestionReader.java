package com.ksanur.questionreader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

/**
 * User: bobacadodl
 * Date: 6/10/14
 * Time: 11:42 PM
 */
public class QuestionReader {

    public static List<Question> questions = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        /*System.out.printf("Parsing PDFs...");
        parsePDFs();
        System.out.println("Finished parsing!");

        saveQuestionsToJSON();*/

        System.out.println("Loading questions...");
        new QuestionReader().loadQuestions();
        Collections.shuffle(questions);
        System.out.println("Loaded "+questions.size()+" questions!");


        JFrame frame = new JFrame("QuestionGUI");
        frame.setContentPane(new QuestionGUI().getPanel());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        //downloadPDFs("http://science.energy.gov/wdts/nsb/high-school/high-school-regionals/hs-rules-forms-resources/sample-science-bowl-questions/");
        //downloadPDFs("http://science.energy.gov/wdts/nsb/middle-school/middle-school-regionals/middle-school-resources/sample-questions/");
    }

    private void loadQuestions() throws IOException {
        String json = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("questions.json"));

        JSONObject obj = new JSONObject(json);
        Iterator<?> keys = obj.keys();

        while(keys.hasNext()){
            String key = (String)keys.next();
            if(obj.get(key) instanceof JSONArray){
                JSONArray arr = obj.getJSONArray(key);

                for(int i=0;i<arr.length();i++) {
                    JSONObject question = arr.getJSONObject(i);
                    Question q = new Question();
                    q.setCategory(Question.Category.getCategory(key));
                    q.setBonus(question.getBoolean("bonus"));
                    q.setQuestion(question.getString("question"));
                    q.setAnswer(question.getString("answer"));
                    if(question.has("multiple_choice")) {
                        q.setMultipleChoice(true);
                        JSONArray mc = question.getJSONArray("multiple_choice");
                        q.setW(mc.getString(0));
                        q.setX(mc.getString(1));
                        q.setY(mc.getString(2));
                        q.setZ(mc.getString(3));
                    }
                    questions.add(q);
                }
            }
        }

    }

    private static void saveQuestionsToJSON() throws IOException {
        JSONObject obj = new JSONObject();
        for(Question.Category cat: Question.Category.values()) {
            obj.put(cat.getName(),new JSONArray());
        }

        for(Question q:questions) {
            JSONObject question = new JSONObject();
            question.put("question",q.getQuestion().trim());
            if(q.isMultipleChoice()) {
                question.put("multiple_choice", new JSONArray().put(q.getW()).put(q.getX()).put(q.getY()).put(q.getZ()));
            }
            question.put("bonus",q.isBonus());
            question.put("answer",q.getAnswer().trim());
            obj.getJSONArray(q.getCategory().getName()).put(question);
        }

        FileWriter file = new FileWriter(new File("questions.json"));
        try {
            file.write(obj.toString(4));
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + obj);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            file.flush();
            file.close();
        }
    }

    private static void parsePDFs() {

        Pattern p = Pattern.compile("^_+");

        try {
            File dir = new File("PDFs");
            dir.mkdirs();

            for(File pdf:dir.listFiles(new PDFFilter())) {
                /*QuestionExtractor extractor = new QuestionExtractor();
                Vector<List<TextPosition>> texts = extractor.getContent(pdDocument);
                for(List<TextPosition> text:texts) {
                    for(TextPosition pos:text) {
                        System.out.print(pos.getCharacter());
                    }
                }
                System.out.println("--------------------------------");
                pdDocument.close();*/

                PDFParser parser = new PDFParser(new FileInputStream(pdf));
                parser.parse();
                COSDocument cosDoc = parser.getDocument();
                PDFTextStripper pdfStripper = new PDFTextStripper();
                PDDocument pdDoc = new PDDocument(cosDoc);
                pdfStripper.setStartPage(1);
                pdfStripper.setEndPage(5);
                String parsedText = pdfStripper.getText(pdDoc);

                String[] lines = parsedText.split("\n");


                Question current = null;
                int part = 0;
                for(String line:lines) {
                    if(line.trim().equalsIgnoreCase("TOSS-UP")) {
                        current = new Question();
                        current.setBonus(false);
                        questions.add(current);
                        part=1;
                    }
                    else if(line.trim().equalsIgnoreCase("BONUS")) {
                        current = new Question();
                        current.setBonus(true);
                        questions.add(current);
                        part=1;
                    }
                    else if(line.startsWith("W)")) {
                        part=2;
                    }
                    else if(line.startsWith("X)")) {
                        part=3;
                    }
                    else if(line.startsWith("Y)")) {
                        part=4;
                    }
                    else if(line.startsWith("Z)")) {
                        part=5;
                    }
                    else if(line.startsWith("ANS")) {
                        part=7;
                    }
                    //detect question

                    if(current!=null) {
                        switch (part) {
                            case 1:
                                if(line.toLowerCase().contains("general science")) {
                                    current.setCategory(Question.Category.GENERAL);
                                }
                                else if(line.toLowerCase().contains("physical science")||line.toLowerCase().contains("physics")) {
                                    current.setCategory(Question.Category.PHYSICAL);
                                }
                                else if(line.toLowerCase().contains("chemistry")) {
                                    current.setCategory(Question.Category.CHEMISTRY);
                                }
                                else if(line.toLowerCase().contains("life science") || line.toLowerCase().contains("biology")) {
                                    current.setCategory(Question.Category.LIFE);
                                }
                                else if(line.toLowerCase().contains("math")) {
                                    current.setCategory(Question.Category.MATH);
                                }
                                else if(line.toLowerCase().contains("earth science") || line.toLowerCase().contains("earth and space")) {
                                    current.setCategory(Question.Category.EARTH_SPACE);
                                }
                                else if(line.toLowerCase().contains("energy")) {
                                    current.setCategory(Question.Category.ENERGY);
                                }

                                if(line.toLowerCase().contains("short answer")) {
                                    current.setMultipleChoice(false);
                                    current.setQuestion(line.split("(?i)short answer")[1].trim());
                                }
                                else if(line.toLowerCase().contains("multiple choice")) {
                                    current.setMultipleChoice(true);
                                    current.setQuestion(line.split("(?i)multiple choice")[1].trim());
                                }
                                else {
                                    current.setQuestion(current.getQuestion()+" "+line.trim());
                                }
                                break;
                            case 2:
                                current.setW(line.trim());
                                break;
                            case 3:
                                current.setX(line.trim());
                                break;
                            case 4:
                                current.setY(line.trim());
                                break;
                            case 5:
                                current.setZ(line.trim());
                                part++;
                                break;
                            case 7:
                                if(line.trim().isEmpty()){
                                    part++;
                                } else if(p.matcher(line.trim()).matches()) {
                                    part++;
                                } else if(line.toLowerCase().contains("page ")) {
                                    part++;
                                }
                                else {
                                    current.setAnswer(current.getAnswer()+line.trim()+" ");
                                }
                                break;
                        }
                    }
                }


                cosDoc.close();
                pdDoc.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadPDFs(String page) {
        try {
            Document doc = Jsoup.connect(page).get();
            Elements links = doc.select("a[href]");

            List<String> pdfUrls = new ArrayList<String>();
            for (Element link : links) {
                String url = link.attr("abs:href");
                if(url.endsWith(".pdf")) {
                    pdfUrls.add(url);
                    System.out.println("* Found PDF: "+getURLFileName(url));
                }
            }

            for(String pdfUrl:pdfUrls) {
                URL url = new URL(pdfUrl.replace(" ","%20"));
                InputStream in = url.openStream();
                File dir = new File("PDFs");
                dir.mkdirs();
                FileOutputStream fos = new FileOutputStream(new File(dir,getURLFileName(pdfUrl)));

                System.out.println("reading file...");
                int length = -1;
                byte[] buffer = new byte[1024];// buffer for portion of data from
                // connection
                while ((length = in.read(buffer)) > -1) {
                    fos.write(buffer, 0, length);
                }
                fos.close();
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getURLFileName(String url) {
        String[] split = url.split("/");
        return split[split.length-1];
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }

    private static class PDFFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".pdf");
        }
    }
}
