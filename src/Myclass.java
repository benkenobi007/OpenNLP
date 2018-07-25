import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Myclass {
    public static String text = "Joe Smith was born in California. " +
            "In 2017, he went to Paris, France in the summer. " +
            "His flight left at 3:00pm on July 10th, 2017. " +
            "After eating some escargot for the first time, Joe said, \"That was delicious!\" " +
            "He sent a postcard to his sister Jane Smith. " +
            "After hearing about Joe's trip, Jane decided she might go to France one day.";
    public static void main(String[] args) {
        /*SentenceModel model = null;
        try (InputStream modelIn = new FileInputStream("en-sent.bin")) {
            model = new SentenceModel(modelIn);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
        String sentences[] = sentenceDetector.sentDetect("  Pierre Vinken, 61 years old, will join the board as a nonexecutive director Nov. 29. Mr. Vinken is " +
                "chairman of Elsevier N.V., the Dutch publishing group. Rudolph Agnew, 55 years" +
                "old and former chairman of Consolidated Gold Fields PLC, was named a director of this" +
                "British industrial conglomerate.\n ");
        for(String s:sentences){
            System.out.println("sentence : "+s);
        }*/
        try {
//            System.out.println("POS Tagging");
//            new Myclass().posTagger();
//            System.out.println();
//            new Myclass().ner();
//            System.out.println("Parsing");
            new Myclass().parseSent();
//            System.out.println();
//            System.out.println("Chunking");
//            new Myclass().chunkSent();
//            new Myclass().tokenizeLang();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    void langDetector(){

    }
    void posTagger() throws IOException {
        String sentence = "The fat cow will jump over the moon";
        FileInputStream fs = new FileInputStream("en-token.bin");
        TokenizerModel tm = new TokenizerModel(fs);
        TokenizerME tokenizer = new TokenizerME(tm);
        String tokens[] = tokenizer.tokenize(sentence);

        fs.close();
        fs = new FileInputStream("en-pos-maxent.bin");
        POSModel pModel = new POSModel(fs);
        POSTaggerME tagger = new POSTaggerME(pModel);
        String tags[] = tagger.tag(tokens);
        double probs[] = tagger.probs();

        System.out.println("Token\t\tTag\t\tProb");
        for(int i=0;i<tokens.length;i++){
            System.out.println(tokens[i] + "\t\t"+tags[i] + "\t\t"+probs[i]);
        }
    }

    void tokenizeLang(){
        //Tokenizer tk = SimpleTokenizer.INSTANCE;

        try{
            FileInputStream fs = new FileInputStream("en-token.bin");
            TokenizerModel mod = new TokenizerModel(fs);
            TokenizerME tk = new TokenizerME(mod);
            Span tkSpans[] = tk.tokenizePos("Hello everybody, how are you?");
            String tokens[] = tk.tokenize("Hello everybody, how are you?");
            /*for(String s:tokens){
                System.out.println(s);
            }*/
            for(Span s : tkSpans){
//                System.out.println(s.getStart());
//                System.out.println(s.getEnd());
                System.out.println(s);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    void ner() throws IOException{
        String sentence = "Mike is senior programming \n" +
                "      manager and Rama is a clerk both are working at \n" +
                "      Tutorialspoint, in New York" ;
        FileInputStream fs = new FileInputStream("en-token.bin");
        TokenizerModel tm = new TokenizerModel(fs);
        TokenizerME tokenizer = new TokenizerME(tm);
        String tokens[] = tokenizer.tokenize(sentence);

        fs.close();
//        fs = new FileInputStream("en-ner-person.bin");
        fs = new FileInputStream("en-ner-location.bin");
        TokenNameFinderModel tmodel = new TokenNameFinderModel(fs);

        NameFinderME namfind = new NameFinderME(tmodel);
        Span names[] = namfind.find(tokens);
        double probs[] = namfind.probs();

        for(Span s : names){
            System.out.println("Type = "+s.getType());
            System.out.println(s.toString() +" "+ tokens[s.getStart()]+ " "+ probs[s.getStart()]);
        }
    }

    void parseSent() throws IOException{
        String sentence = "The fat cow will jump over the moon";
        FileInputStream fs = new FileInputStream("en-parser-chunking.bin");
        ParserModel parse_Model = new ParserModel(fs);
        Parser parser = ParserFactory.create(parse_Model);
        Parse[] parses = ParserTool.parseLine(text,parser,1);

        for(Parse p : parses)
            p.show();
    }

    void chunkSent() throws IOException{
        Tokenizer tk = SimpleTokenizer.INSTANCE;
        String sentence = "The fat cow will jump over the moon.";
        String[] tokens = tk.tokenize(sentence);

        InputStream fs = new FileInputStream("en-pos-maxent.bin");
        POSModel pmodel = new POSModel(fs);
        POSTaggerME tagger = new POSTaggerME(pmodel);
        String[] tags = tagger.tag(tokens);
        fs.close();

        fs = new FileInputStream("en-chunker.bin");
        ChunkerModel chmodel = new ChunkerModel(fs);
        ChunkerME chunker = new ChunkerME(chmodel);
        String chunks[] = chunker.chunk(tokens,tags);
        Span[] spans = chunker.chunkAsSpans(tokens,tags);
        double[] probs = chunker.probs();

        System.out.println("Strings");
        for(String s:chunks){
            System.out.println(s);
        }
        /*
        for(double d:probs){
            System.out.println(d);
        }*/

        System.out.println("Spans");
        for(Span s : spans){
            System.out.println(s);
//            System.out.println(s.getType());
        }
    }
}
