package main.java.SentenceSimplification;

import edu.stanford.nlp.trees.Tree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Wrapper {

    public static void main(String[] args) {
        String document = "C:/Software/xampp/htdocs/glint/resources/passage.txt";

        //generate parse trees for each sentence in the document
        ParseTreeGenerator parseTreeGenerator = new ParseTreeGenerator();
        List<Tree> parseTrees = parseTreeGenerator.getParseTreesForDocument(document);

        //perform pronoun noun phrase coreference resolution using arkref
        CoreferenceResolver coreferenceResolver = new CoreferenceResolver();
        coreferenceResolver.resolveCorefence(parseTrees);

        //perform sentence simplification
        SentenceSimplifier sentenceSimplifier = new SentenceSimplifier();
        List<Question> trees = new ArrayList<>();
        Collection<Question> tmpSet;

        int sentnum = 0;
        for (Tree sentence : parseTrees) {
            if (AnalysisUtilities.filterOutSentenceByPunctuation(AnalysisUtilities.orginialSentence(sentence.yield()))) {
                sentnum++;
                continue;
            }

            tmpSet = sentenceSimplifier.simplify(sentence, false);
            for (Question q : tmpSet) {
                q.setSourceSentenceNumber(sentnum);
                q.setSourceDocument(coreferenceResolver.getDocument());
            }
            trees.addAll(tmpSet);

            sentnum++;
        }

        //add new sentences with clarified/resolved NPs
        trees.addAll(coreferenceResolver.clarifyNPs(trees, true, false));


        StringBuilder sb = new StringBuilder();
        //upcase the first tokens of all output trees.
        for (Question q : trees) {
            AnalysisUtilities.upcaseFirstToken(q.getIntermediateTree());
            List<Tree> simplifiedSentence = q.getIntermediateTree().getLeaves();
            for (Tree s : simplifiedSentence) {
                sb.append(s.toString());
                sb.append(" ");
            }
            sb.append("\n");
        }
        try {
            File file = new File("C:/Software/xampp/htdocs/glint/resources/simplifiedSentences.txt");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(sb.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //generate how and why questions
        QuestionGenerator questionGenerator = new QuestionGenerator();
        List<QuestionAnswer> howAndWhyQuestions = questionGenerator.generateQuestions(trees);
        StringBuilder howWhyQA = new StringBuilder();
        for(QuestionAnswer qa: howAndWhyQuestions){
            howWhyQA.append(qa.getQuestion()).append(":").append(qa.getAnswer()).append(",");
            System.out.println("Question: "+qa.getQuestion() + " Answer: "+ qa.getAnswer());
        }
        if(howWhyQA.length()>0)
            howWhyQA.setLength(howWhyQA.length() - 1);

        try {
            File file = new File("C:/Software/xampp/htdocs/glint/resources/qa_part1.txt");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(howWhyQA.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ProcessBuilder pb = new ProcessBuilder("py","C:/Software/xampp/htdocs/glint/php/MC/app1.py");//// , "--arg1" , "C:/Software/xampp/htdocs/glint/resources/simplifiedSentences.txt");
           // ProcessBuilder pb = new ProcessBuilder("py","C:/Software/xampp/htdocs/glint/php/test.py"); //, "--arg1" , "C:/Software/xampp/htdocs/glint/php/SentenceSimplification1/out/artifacts/SentenceSimplification1_jar/simplifiedSentences.txt");
            Process p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
