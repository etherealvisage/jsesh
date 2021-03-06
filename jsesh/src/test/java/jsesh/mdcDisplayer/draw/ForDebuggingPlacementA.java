/*
 * Copyright ou © ou Copr. Serge Rosmorduc (2016) 
 * serge.rosmorduc@cnam.fr

 * Ce logiciel est un programme informatique servant à mettre en place 
 * une base de données linguistique pour l'égyptien ancien.

 * Ce logiciel est régi par la licence CeCILL soumise au droit français et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence [CeCILL|CeCILL-B|CeCILL-C] telle que diffusée par le CEA, le CNRS et l'INRIA 
 * sur le site "http://www.cecill.info".

 * En contrepartie de l'accessibilité au code source et des droits de copie,
 * de modification et de redistribution accordés par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
 * seule une responsabilité restreinte pèse sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les concédants successifs.

 * A cet égard  l'attention de l'utilisateur est attirée sur les risques
 * associés au chargement,  à l'utilisation,  à la modification et/ou au
 * développement et à la reproduction du logiciel par l'utilisateur étant 
 * donné sa spécificité de logiciel libre, qui peut le rendre complexe à 
 * manipuler et qui le réserve donc à des développeurs et des professionnels
 * avertis possédant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
 * logiciel à leurs besoins dans des conditions permettant d'assurer la
 * sécurité de leurs systèmes et ou de leurs données et, plus généralement, 
 * à l'utiliser et l'exploiter dans les mêmes conditions de sécurité. 

 * Le fait que vous puissiez accéder à cet en-tête signifie que vous avez 
 * pris connaissance de la licence CeCILL, et que vous en avez accepté les
 * termes.
 */
package jsesh.mdcDisplayer.draw;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import jsesh.editor.JMDCEditor;

/**
 * A small test for debugging placement when signs are scaled (or when the
 * dimensions are not the usual ones). TODO: make this into a proper test. Those
 * tests should produce graphical files, which should be checked manually.
 *
 * @author rosmord
 */
public class ForDebuggingPlacementA {

    JMDCEditor editor = new JMDCEditor();
    JFrame frame = new JFrame("testA - scaling");

    public ForDebuggingPlacementA() {
        String mdc = "+lCheck advance geometry combined with scaling+s!"
                + "t&m-m&t-t&m&t-t^^^m-m&&&t!"
                + "t&A-A&t-t&A&t-t^^^A-A&&&t!"
                + "G5&&&ra-G5\\200&&&ra-!"
                + "t^^^A-t^^^A\\R180+i (unexpectedly --- seems to work)+s-!"
                + "F20\\R180&&&x-+i (doesn't work; it's more or less expected)+s!"
                + "t^^^A\\200\\R180+i (this one is not expected to work now)+s-!"; 
        
        editor.setMDCText(mdc);
        frame.add(editor);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ForDebuggingPlacementA());
    }
}
