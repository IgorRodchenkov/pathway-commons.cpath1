package org.mskcc.pathdb.taglib;

import java.util.StringTokenizer;
import java.util.HashSet;

/**
 * Hack to Parse Reactome Comments.
 *
 * @author Ethan Cerami
 */
public class ReactomeCommentUtil {
    private static HashSet reactomeWordHack;

    /**
     * Massage Reactome Comments.
     * @param originalComment Original comment.
     * @return massaged HTML comment.
     */
    public static String massageComment(String originalComment) {
        if (reactomeWordHack == null) {
            initReactomeWordHack();
        }

        originalComment = originalComment.replaceAll("<BR>", "<p>");
        originalComment = originalComment.replaceAll("<br>", "<p>");

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
        StringTokenizer tokenizer = new StringTokenizer (originalComment, " ");

        //  This is a mini hack to parse Reactome comments
        int tokenNum = 0;
        while (tokenizer.hasMoreElements()) {
            String token = (String) tokenizer.nextElement();
            if (isMagicReactomeWord(token) && tokenNum > 0) {
                buf.append ("<P>");
            }
            buf.append (token + " ");
            tokenNum++;
        }
        return buf.toString();
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
        reactomeWordHack.add("CAUTION:");
        reactomeWordHack.add("PTM:");
        reactomeWordHack.add("INDUCTION:");
        reactomeWordHack.add("DISEASE:");
    }
}
