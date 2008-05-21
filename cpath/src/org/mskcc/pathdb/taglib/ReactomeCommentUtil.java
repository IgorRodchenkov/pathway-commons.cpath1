package org.mskcc.pathdb.taglib;

import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * Hack to Parse Reactome Comments.
 *
 * @author Ethan Cerami
 */
public class ReactomeCommentUtil {
    private static HashSet reactomeWordHack;

    /**
     * Massage Reactome Comments.
     *
     * @param originalComment Original comment.
     * @return massaged HTML comment.
     */
    public static String massageComment(String originalComment) {
        if (reactomeWordHack == null) {
            initReactomeWordHack();
        }

        //  Ugly hack to handle Reactome <P>'s and <BR>'s
        originalComment = originalComment.replaceAll("<BR>", "<p>");
        originalComment = originalComment.replaceAll("<br>", "<p>");
        originalComment = originalComment.replaceAll("<p><p><p>", "");
        originalComment = originalComment.replaceAll("<p><p>", "<p>");
        if (originalComment.endsWith("<p>")) {
            originalComment = originalComment.substring(0, originalComment.length() - 3);
        }

        //  Ugly hack to handle Reactome HREF links
        if (originalComment.indexOf("<a href") > -1) {
            originalComment = originalComment.replaceAll
                    ("<a href='/electronic_inference.html' target = 'NEW'>",
                            "<a href='http://reactome.org/electronic_inference.html' target = 'NEW'>");
            originalComment = originalComment.replaceAll("For details on the OrthoMCL system "
                    + "see also:",
                    "For details on the OrthoMCL system see also:  "
                            + "[<A HREF='http://www.ncbi.nlm.nih.gov:80/entrez/query.fcgi?"
                            + "cmd=Retrieve&db=PubMed&dopt=Abstract&list_uids=12952885' target='NEW'>"
                            + "Li <I>et al"
                            + "</I> 2003</A>]");
        }

        StringBuffer buf = new StringBuffer();
        StringTokenizer tokenizer = new StringTokenizer(originalComment, " ");

        //  This is a mini hack to parse Reactome comments
        int tokenNum = 0;
        while (tokenizer.hasMoreElements()) {
            String token = (String) tokenizer.nextElement();
            if (isMagicReactomeWord(token) && tokenNum > 0) {
                buf.append("<p>");
            }
            buf.append(token + " ");
            tokenNum++;
        }
        return buf.toString();
    }


    /**
     * Hack to chop Reactome comments into pieces.
     *
     * @param originalComment Original comment.
     * @return original comment, chopped up into bite sized pieces.
     */
    public static String[] chopComments(String originalComment) {
        if (reactomeWordHack == null) {
            initReactomeWordHack();
        }
        StringBuffer buf = new StringBuffer();
        StringTokenizer tokenizer = new StringTokenizer(originalComment, " ");
        int tokenNum = 0;
        while (tokenizer.hasMoreElements()) {
            String token = (String) tokenizer.nextElement();
            if (isMagicReactomeWord(token) && tokenNum > 0) {
                buf.append("###");
            }
            buf.append(token + " ");
            tokenNum++;
        }
        String temp = buf.toString();
        return temp.split("###");
    }

    private static boolean isMagicReactomeWord(String text) {
        if (reactomeWordHack.contains(text)) {
            return true;
        } else {
            return false;
        }
    }

    private static void initReactomeWordHack() {
        reactomeWordHack = new HashSet();
        reactomeWordHack.add("FUNCTION:");
        reactomeWordHack.add("ENZYME");
        reactomeWordHack.add("SUBUNIT:");
        reactomeWordHack.add("SUBCELLULAR");
        reactomeWordHack.add("TISSUE");
        reactomeWordHack.add("SIMILARITY:");
        reactomeWordHack.add("DOMAIN:");
        reactomeWordHack.add("ALTERNATIVE");
        reactomeWordHack.add("PTM:");
        reactomeWordHack.add("INDUCTION:");
        reactomeWordHack.add("DISEASE:");
        reactomeWordHack.add("COPYRIGHT:");
        reactomeWordHack.add("SEQUENCE");
    }
}
