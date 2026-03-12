package be.ulb.dblp;


/**
 * Entry point for the DBLP Community Analysis tool.
 *
 * <p>Usage:
 * <pre>
 *   java -jar dblp-community-analysis.jar &lt;dblp.xml.gz&gt; &lt;dblp.dtd&gt;
 * </pre>
 *
 * <ul>
 *   <li>Argument 0 – path to the gzip-compressed DBLP XML dump</li>
 *   <li>Argument 1 – path to the DBLP DTD file</li>
 * </ul>
 */
public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: dblp-community-analysis.jar <dblp.xml.gz> <dblp.dtd>");
            System.exit(1);
        }

        String xmlGzPath = args[0];
        String dtdPath   = args[1];

        System.out.println("DBLP Community Analysis");
        System.out.println("  XML  : " + xmlGzPath);
        System.out.println("  DTD  : " + dtdPath);
        System.out.println();

        // TODO: Task 1 – undirected co-authorship graph → connected components
        // Task1Runner.run(xmlGzPath, dtdPath, "results/task1/");

        // TODO: Task 2 – directed collaboration graph → strongly connected components
        // Task2Runner.run(xmlGzPath, dtdPath, "results/task2/");

        System.out.println("Done.");
    }
}
