<%@ page import="java.util.ArrayList,
                 org.mskcc.pathdb.model.Protein,
                 org.mskcc.pathdb.model.Interaction"%>
<%
    ArrayList interactions = (ArrayList) request.getAttribute("interactions");

    //  If interactions object is null, create some sample data.
    if (interactions == null) {
        interactions = new ArrayList();
        Interaction interaction = new Interaction();
        Protein proteinA = new Protein ();
        proteinA.setOrfName("proteinA");
        interaction.setNodeA(proteinA);
        Protein proteinB = new Protein ();
        proteinB.setOrfName("proteinB");
        interaction.setNodeB(proteinB);
        interaction.setExperimentalSystem("Yeast Two Hybrid");
        interactions.add(interaction);
        interactions.add(interaction);
        interactions.add(interaction);
    }
%>

<table><tr><td class="heading1">Interactions</td></tr><tr><td class="heading2">Interactor</td><td class="heading2">Interactor</td><td class="heading2">Experimental System</td></tr><tr><td>YCR038C</td><td>YAL036C</td><td>classical two hybrid</td></tr><tr><td>YCR038C</td><td>YDL065C</td><td>classical two hybrid</td></tr><tr><td>YCR038C</td><td>YDR532C</td><td>classical two hybrid</td></tr><tr><td>YCR038C</td><td>YEL061C</td><td>classical two hybrid</td></tr><tr><td>YCR038C</td><td>YHR119W</td><td>classical two hybrid</td></tr><tr><td>YBR200W</td><td>YCR038C</td><td>Synthetic Lethality</td></tr></table>

<TABLE>
    <TR>
        <TD COLSPAN=3 class="header2">Matching Interactions</TD>
    </TR>
    <TR>
        <TH>Protein A</TH>
        <TH>Protein B</TH>
        <TH>Experimental System</TH>
    </TR>
<%
    if (interactions != null) {
        for (int i=0; i<interactions.size(); i++) {
            Interaction interaction = (Interaction) interactions.get(i);
            Protein proteinA = interaction.getNodeA();
            Protein proteinB = interaction.getNodeB();
            String expSystem = interaction.getExperimentalSystem();
%>
    <TR>
        <TD><%= proteinA.getOrfName() %></TD>
        <TD><%= proteinB.getOrfName() %></TD>
        <TD><%= expSystem %></TD>
        </TR>
<%      }
    }
%>
</TABLE>