package exam_socketsandthreads;
  
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TurnStile {

    
    
    public static void main(String[] args) throws IOException {
        
        Even even = new Even();
        String ip = "localhost";
        int port = 3344;
        int id = 0;
        HashMap<Integer, TurnStile> stiles = new HashMap();
        
        if (args.length == 2) {
            System.out.println("Args found -> Setting IP and PORT");
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }

        for (Map.Entry<Integer, TurnStile> entrySet : stiles.entrySet()) {
            Integer key = entrySet.getKey();
            TurnStile value = entrySet.getValue();

            if (key == id) {
                id++;
            }
        }

        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress(ip, port));
        

        while (true) {
            TurnStileThread ts = new TurnStileThread(ss.accept(), id, even);
            Thread t1 = new Thread(ts);
            t1.start();
            id++;
        }
    }
    
    public static class TurnStileThread implements Runnable {

        Socket s;
        BufferedReader in;
        PrintWriter out;
        Even even;
        int turnStile_Id;
        long tsNumber = 0;
         HashMap<Integer, TurnStile> stiles;
        
        public TurnStileThread(Socket socket, int id, Even even) {
            s = socket;
            turnStile_Id = id;
            this.even = even;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new PrintWriter(s.getOutputStream(), true);

                out.println("TURNSTILE-" + turnStile_Id);

                while (true) {
                    String temp = in.readLine().toLowerCase();
                    switch (temp) {
                        case "pass":
                            tsNumber++;
                            even.setVisitorsNumber();
                            
                            out.println("Number from this turnStile: " + tsNumber);
                            break;
                        case "total":
                            out.println("Total visitors: " + even.getVisitorsNumber());
                            break;
                        default:
                            out.println("Command not found");
                            
                        
                    }
                    if (in.readLine().equalsIgnoreCase("pass")) {
                        tsNumber++;
                        even.setVisitorsNumber();
                    }

                    
                }

            } catch (IOException ex) {
                Logger.getLogger(TurnStile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static class Even {
        long visitorsNumber = 0;

        public synchronized long getVisitorsNumber() {
            return visitorsNumber;
        }

        public synchronized void setVisitorsNumber() {
            visitorsNumber++;
        }
    }
}

//  COMMENTS
//
//  When and why we will use Threads in our programs?
//  Når man vil udnytte at man kan arbejde på flere kerner (og dermed kører flere metoder på samme tid).
//  Det gør også at programmer kan arbejde med nogle tungere ting i baggrunden, mens andre metoder kører.
//
//  Explain about the Race Condition Problem and ways to solve it in Java
//  Race Conditionen opstår når to tråde kører og bruger samme variabler uden at tage hensyn til at den måske bliver tilgået/ændret af en anden tråd (se Even opgaven).
//  Syncronize gør at det kun er een tråd der har adgang til en variable eller metode af gangen. Alternativt kan man også bruge lock for at låse den del af metoden
//  (eller hele metoden) som man vil have at kun een tråd har adgang til af gangen. 
//
//  Explain about deadlocks, how to detect them and ways to solve the Deadlock Problem
//  Deadlocks er når tråde der kræver adgang til metoder som andre tråde har låst og derfor ikke kan tilgå dem. Hvis man f.eks. har to tråde kører metoder (som de hver især har låst)
//  så skal have adgang til den anden tråds metode. på den måde låser de for hinanden.
//
//  Der er umiddelbart flere muligheder for at fjerne deadlocks på.
//  - En er at stoppe alle tråde.
//  - Man kan også stoppe én tråd af gangen og se om det hjælper.
//


