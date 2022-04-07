import java.io.File;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;


public class Dictionary {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED = "\u001B[31m";

    private IDictionary<String,String> dict = new SortedArrayDictionary<>();

    public void init() {
        Scanner in = new Scanner(System.in);
            
        System.out.println("Willkommen");
        displayMenu();

        while (true) {
            System.out.print("Eingabe: ");
            String n = in.nextLine();
            n = n.toLowerCase();

            if (n.equals("exit")) {
                System.out.println("Auf Wiedersehen");
                break;
            } else if (n.equals("h")) {
                displayMenu();
            } else if (n.startsWith("i ")) {
                userInsert(n, dict);                
            } else if (n.startsWith("s ")) {
                userSearch(n, dict);
            } else if (n.startsWith("create ")) {
                userCreate(n, dict);
            } else if (n.startsWith("read ")) {
                userRead(n, dict);
            } else if (n.startsWith("p ") || n.equals("p")) {
                userPrint(n, dict);
            }
            else if (n.startsWith("r ")) {
                userRemove(n, dict);
            }
            else {
                System.out.println("  Unbekannter Befehl: " + n + " Hilfe benötigt? Gebe \""+ ANSI_YELLOW + "h" + ANSI_RESET + "\" ein");
            }
        } 
        in.close();
    }

    private void displayMenu() {

        System.out.printf("\nBefehle:\n");
        System.out.printf(ANSI_YELLOW + "  create" + ANSI_RESET + " \"Implementierung\" - um ein neues Dictionary anzulegen (Default: SortedArrayDictionary), verfügbar: BinaryTreeDictionary, HashDictionary, SortedArrayDictionary\n");
        System.out.printf(ANSI_YELLOW + "  read" + ANSI_RESET + " [n] \"Dateiname\" - um die ersten n (optional) Einträge einzulesen\n");
        System.out.printf(ANSI_YELLOW + "  p" + ANSI_RESET + " [n] - um die ersten n (optional) Einträge des Dictionaries auszugeben\n");
        System.out.printf(ANSI_YELLOW + "  s" + ANSI_RESET + " \"deutsch\" - Suche nach einem Eintrag\n");
        System.out.printf(ANSI_YELLOW + "  i" + ANSI_RESET + " \"deutsch\" \"englisch\" - um ein neues Wortpaar einzufügen\n");
        System.out.printf(ANSI_YELLOW + "  r" + ANSI_RESET + " [-a] \"deutsch\" - um einen bzw. alle Einträge zu entfernen\n");
        System.out.printf(ANSI_YELLOW + "  exit" + ANSI_RESET + " - um das Programm zu beenden\n");
        System.out.printf(ANSI_YELLOW +"  h" + ANSI_RESET + " - um dieses Menü anzuzeigen\n\n");
    }

    private void userInsert(String input, IDictionary<String,String> dict) {
        String[] params = input.split(" ");
                if (params.length != 3) {
                    System.out.println("  Erwartete Eingabe vom Typ i \"x\" \"y\" Eingabe: " + input);
                    return;
                }
                String k = dict.insert(params[1], params[2]);

                if (k == null) {
                    System.out.println("  Einfügen erfolgreich: " + params[1] + " " + params[2] + " Größe: " + dict.size());
                } else {
                    System.out.println("  Überschreiben erfolgreich: Alt: " +  k + " Neu: " + params[2]);
                }
    }

    private void userSearch(String input, IDictionary<String,String> dict) {
        String[] params = input.split(" ");
                if (params.length != 2) {
                    System.out.println("  Erwartete Eingabe vom Typ s \"x\" Eingabe: " + input);
                    return;
                }
                if (dict.size() == 0) {
                    System.out.println(ANSI_RED + "  Dictionary ist leer - keine Suche möglich" + ANSI_RESET);
                    return;
                }
                long start = System.nanoTime();
                String k = dict.search(params[1].strip());
                if (k != null) {
                    System.out.println("  Gefunden: " + k);
                } else {
                    System.out.println("  Kein Eintrag für " + params[1] + " gefunden");
                }
                long end = System.nanoTime();
                System.out.println("  Benötigte Zeit in mikrosekunden: " + (end-start)/1000);
    }

    private void userCreate(String input, IDictionary<String,String> dict) {
                String[] params = input.split(" ");
                int pLength = params.length;

                if (pLength < 2 && pLength > 3) {
                    System.out.println("  Erwartete Eingabe vom Typ create \"x\" ([asc|desc] für BTD oder SAD) Eingabe: " + input);
                    return;
                }
                if (params[1].contains("binary")) {
                    System.out.print(" Initialisiere neues BinaryTreeDictionary");
                    if (pLength == 3) {
                        if (params[2].startsWith("d")) {
                            System.out.print(" descending\n");
                            this.dict = new BinaryTreeDictionary<>(Comparator.naturalOrder());
                            return;
                        }
                    } 
                    System.out.print(" ascending\n");
                    this.dict = new BinaryTreeDictionary<>(Comparator.reverseOrder());

                } else if (params[1].contains("hash")){
                    if (pLength == 3) {
                        System.out.println(ANSI_RED + "  Sortieroptionen sind nur für SAD und BTD verfügbar " + ANSI_RESET);
                    }
                    System.out.println(" Initialisiere neues HashDictionary");
                    this.dict = new HashDictionary<String, String>();
                } else {
                    System.out.print(" Initialisiere neues SortedArrayDictionary");
                    if (pLength == 3) {
                        if (params[2].startsWith("d")) {
                            System.out.print(" descending\n");
                            this.dict = new SortedArrayDictionary<>(Comparator.naturalOrder());
                            return;
                        }
                    }
                    System.out.print(" ascending\n");
                    this.dict = new SortedArrayDictionary<>(Comparator.reverseOrder());
                }
    }

    private void userRead(String input, IDictionary<String,String> dict) {
        String[] params = input.split(" ");
                if (params.length == 3) {
                    int noOfEntries = 100;

                    try {
                        noOfEntries = Integer.parseInt(params[1]);
                        File file = new File(params[2].replace("\\", "\\\\"));
                        Scanner sc = new Scanner(file);

                        int count = 0;
                        long start = System.nanoTime();
                        while (sc.hasNext() && count < noOfEntries) {
                            String[] e = sc.nextLine().split(" ");
                            if (e.length == 2) {
                                dict.insert(e[0], e[1]);
                            } else {
                                continue;
                            }
                            count++;
                        }
                        long end = System.nanoTime();

                        sc.close();

                        System.out.println("\n" + "  " + count + " Einträge eingefügt\n");
                        System.out.println("  Benötigte Zeit in ms: " + (end-start) / 1000000);

                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                } else if (params.length == 2) {

                    try {
                        File file = new File(params[1].replace("\\", "\\\\"));
                        Scanner sc = new Scanner(file);

                        int inserts = 0;
                        int count = 0;
                        long start = System.nanoTime();
                        while (sc.hasNext()) {
                            String[] e = sc.nextLine().split(" ");
                            if (e.length == 2) {
                                String t = dict.insert(e[0], e[1]);
                                if (t == null) {
                                    inserts++;
                                }
                            } else {
                                continue;
                            }
                            count++;
                        }
                        long end = System.nanoTime();
                        sc.close();

                        System.out.println("\n" + "  " + inserts + " Einträge eingefügt " + (count - inserts) + " Einträge überschrieben\n");
                        System.out.println("  Benötigte Zeit in ms: " + (end-start) / 1000000);

                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
    }

    private void userPrint(String input, IDictionary<String,String> dict) {
        String[] params = input.split(" ");
                int k = dict.size();

                if (params.length == 2) {
                    if (Integer.parseInt(params[1]) <= dict.size()) {
                        k = Integer.parseInt(params[1]);
                    }
                }
                
                Iterator<IDictionary.Entry<String,String>> it = dict.iterator();

                int count = 0;
                System.out.println();

                while (it.hasNext() && count < k) {
                    System.out.println("  " + it.next());
                    count++;
                }

                System.out.println("\n  Ausgabe: " + count + " Einträge " + count + "/" + dict.size() + "\n");
    }

    private void userRemove(String input, IDictionary<String,String> dict) {
        String[] params = input.split(" ");
                if (params.length != 2) {
                    System.out.println("  Erwartete Eingabe vom Typ r \"x\" oder r -a Eingabe: " + input);
                    return;
                }

                if (params[1].equals("-a")) {
                    int size = dict.size();
                    dict.wipe();
                    System.out.println("  Alle Einträge entfernt insgesamt: " + size);
                } else {
                    String k = dict.remove(params[1].strip());
                    if (k != null) {
                        System.out.println("  Entfernt: " + k);
                    } else {
                        System.out.println("  Kein Eintrag für " + params[1] + " gefunden - unverändert");
                    }
                }
    }
}
