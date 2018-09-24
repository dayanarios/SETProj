/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.documents.JsonFileDocument;
import cecs429.index.InvertedIndex;
import cecs429.index.Positional_inverted_index;
import cecs429.index.Positional_posting;
import cecs429.text.EnglishTokenStream;
import cecs429.text.NewTokenProcessor;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author dayanarios
 */
public class DocumentIndexer {
    protected static DocumentCorpus corpus;
    private static InvertedIndex index; 
    private static Positional_inverted_index index2;
    protected static String query;
    protected static List<Positional_posting> postings; 
   
    
    /**
     * 
     * @param path supplied by user in GUI.SearchDirectoriesButtonActionPerformed
     * 
     */
    protected static void startIndexing(Path path) throws ClassNotFoundException, InstantiationException, IllegalAccessException
   {
       
       corpus = DirectoryCorpus.loadJsonTextDirectory(path.toAbsolutePath(), ".json");
       //corpus = DirectoryCorpus.loadTextDirectory(path.toAbsolutePath(), ".txt"); 
       index2 = posindexCorpus(corpus);
       //index = indexCorpus(corpus); 
        
   }
    
   /*
   protected static void startSearchEngine() throws ClassNotFoundException, InstantiationException, IllegalAccessException 
   {
         // TODO: fix this application so the user is asked for a term to search.
        boolean cont = true; 

        Scanner scan = new Scanner(System.in);
         
        List<String> queries; 
        
        NewTokenProcessor processor = new NewTokenProcessor();  

        
        //System.out.println("User is inputting a query"); 
        queries = new ArrayList(processor.processToken(query)); 

        if (query.equals("quit")){
            cont = false;
            System.exit(0);

        }

        for(String q : queries)
        {
            if(index.getPostings(q).isEmpty())
            {
                //clears list and repopulates it 
                String notFound = "Your search " + query + " is not found in any documents";
                GUI.JListModel.clear();
                GUI.JListModel.addElement(notFound); 
                
            }
            else
            {
               //clears list and repopulates it 
               String docInfo;
               GUI.JListModel.clear();
               
               
               for (Posting p : index.getPostings(q)) 
               { 
                   docInfo = "Document Title: " + corpus.getDocument(p.getDocumentId()).getTitle();
                   GUI.JListModel.addElement(docInfo); 
                
                } 
            }
        }
        
        GUI.SearchBarTextField.setText("Enter a new search or 'quit' to exit");
        GUI.SearchBarTextField.selectAll();
                
            
            
        
        
   }
   */
   
   
   protected static void startSearchEngine() throws ClassNotFoundException, InstantiationException, IllegalAccessException 
   {
       
         // TODO: fix this application so the user is asked for a term to search.
        boolean cont = true; 

        Scanner scan = new Scanner(System.in);
        
        List<String> queries; 
        
        NewTokenProcessor processor = new NewTokenProcessor();  

        
            //System.out.println("User is inputting a query"); 
            queries = new ArrayList(processor.processToken(query)); 

            if (query.equals("quit")){
                cont = false;
                System.exit(0);
            }

            for(String q : queries)
            {
                if(index2.getPositional_posting(query).isEmpty()) //might be giving the error
                {
                    //clears list and repopulates it 
                    String notFound = "Your search " + query + " is not found in any documents";
                    GUI.JListModel.clear();
                    GUI.JListModel.addElement(notFound); 
                }
                else
                {
                    //clears list and repopulates it 
                   String docInfo;
                   GUI.JListModel.clear();
                   postings = index2.getPositional_posting(query); 
                   
                   for (Positional_posting p : postings)
                   {
                       docInfo = corpus.getDocument(p.getDocumentId()).getTitle(); //+ " positions: "+ p.getPositions();
                       GUI.JListModel.addElement(docInfo); 

                    } 
                }
            }

        
        GUI.SearchBarTextField.setText("Enter a new search or 'quit' to exit");
        GUI.SearchBarTextField.selectAll();
        
   }
   
   
    
    
    private static InvertedIndex indexCorpus(DocumentCorpus corpus) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        
        //BasicTokenProcessor processor = new BasicTokenProcessor();
        NewTokenProcessor processor = new NewTokenProcessor(); 

        // First, build the vocabulary hash set.
        // TODO:
        // Get all the documents in the corpus by calling GetDocuments().
        Iterable<Document> docs = corpus.getDocuments(); //call registerFileDocumentFactory first?
        
        InvertedIndex index = new InvertedIndex(); 

        // Iterate through the documents, and:
        for(Document d : docs)
        {
            // Tokenize the document's content by constructing an EnglishTokenStream around the document's content.
            Reader reader = d.getContent(); 
            EnglishTokenStream stream = new EnglishTokenStream(reader); //can access tokens through this stream

            // Iterate through the tokens in the document, processing them using a BasicTokenProcessor,
            //		and adding them to the HashSet vocabulary.
            Iterable<String> tokens = stream.getTokens();

            for(String t : tokens)
            {

                List<String> terms = new ArrayList(processor.processToken(t));   
                
                
                for(String term: terms)
                {
                    index.addTerm(term, d.getId()); 
                }
                
                

            }

        }
        
 
        
        return index; 
    }
    
    
    private static Positional_inverted_index posindexCorpus(DocumentCorpus corpus) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        
        //BasicTokenProcessor processor = new BasicTokenProcessor();
        NewTokenProcessor processor = new NewTokenProcessor(); 
        
        Iterable<Document> docs = corpus.getDocuments(); 
        
        Positional_inverted_index index = new Positional_inverted_index(); 

        // Iterate through the documents, and:
        for(Document d : docs)
        {
            // Tokenize the document's content by constructing an EnglishTokenStream around the document's content.
            Reader reader = d.getContent(); 
            EnglishTokenStream stream = new EnglishTokenStream(reader); //can access tokens through this stream

            // Iterate through the tokens in the document, processing them using a BasicTokenProcessor,
            //		and adding them to the HashSet vocabulary.
            Iterable<String> tokens = stream.getTokens();
            int i=0;
            for(String t : tokens)
            {

                List<String> terms = new ArrayList(processor.processToken(t)); 
                
                for (String term: terms) 
                {
                    
                    //we pass term, list<hashmap>,docid
                    index.addTerm(term, i, d.getId()); 
                    
                }
                i=i+1;
    
            }

        }
        
 
        return index; 
    }
    
    /**
     * Use this to run the program without the GUI.
     * Comment out original startSearchEngine functions. 
     * startSearchEngine is for the inverted index
     * startSearchEngine2 is for the positional index 
     */
    /*
    protected static void startSearchEngine(DocumentCorpus corpus, Index index) throws ClassNotFoundException, InstantiationException, IllegalAccessException 
   {
       
       
        
         // TODO: fix this application so the user is asked for a term to search.
        boolean cont = true; 

        Scanner scan = new Scanner(System.in);
        String query; 
        List<String> queries; 
        
        NewTokenProcessor processor = new NewTokenProcessor();  

        while(cont)
        {
            System.out.println("\nEnter a term to search (single word only): ");
            query = scan.nextLine();
            queries = new ArrayList(processor.processToken(query)); 

            if (query.equals("quit")){
                cont = false;
                break;
            }

            for(String q : queries)
            {
                if(index.getPostings(q).isEmpty())
                {
                    System.out.println(q + " not found in vocabulary");
                }
                else
                {
                    System.out.println("Documents that contain the query: " + query); 
                   for (Posting p : index.getPostings(q)) { 

                    System.out.println("Document Title: " + corpus.getDocument(p.getDocumentId()).getTitle());// + " positions: "+ p.getPositions());
                    } 
                }
            }

        }
        
        System.exit(0);
   }
   
   
   
   protected static void startSearchEngine2(DocumentCorpus corpus, Index2 index) throws ClassNotFoundException, InstantiationException, IllegalAccessException 
   {
       
       
        
         // TODO: fix this application so the user is asked for a term to search.
        boolean cont = true; 

        Scanner scan = new Scanner(System.in);
        String query; 
        List<String> queries; 
        
        NewTokenProcessor processor = new NewTokenProcessor();  

        while(cont)
        {
            System.out.println("\nEnter a term to search (single word only): ");
            query = scan.nextLine();
            queries = new ArrayList(processor.processToken(query)); 

            if (query.equals("quit")){
                cont = false;
                break;
            }

            for(String q : queries)
            {
                if(index.getPositional_posting(query).isEmpty())
                {
                    System.out.println(q + " not found in vocabulary");
                }
                else
                {
                    System.out.println("Documents that contain the query: " + query); 
                   for (Positional_posting p : index.getPositional_posting(query)){

                    System.out.println("Document Title: " + corpus.getDocument(p.getDocumentId()).getTitle() + " positions: "+ p.getPositions());
                    } 
                }
            }

        }
        
        System.exit(0);
   }
   
    */
    
    
}
