/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 *
 * ludovic.apvrille AT enst.fr
 *
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */


package graph;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import myutil.TraceManager;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.util.ShortcutManager;
import ui.file.PNGFilter;
import ui.util.IconManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;


/**
 * Class AUTGraphDisplay
 * Creation : 01/12/2016
 * * @version 1.0 01/12/2016
 *
 * @author Ludovic APVRILLE
 */
public class AUTGraphDisplay implements MouseListener, ViewerListener, Runnable {

    protected static String STYLE_SHEET = "node {fill-color: #B1CAF1; text-color: black; size: 20px, 20px; text-size:14;}     " +
            "edge {text-color: black; shape: cubic-curve; text-size:10;}    " +
            "edge.defaultedge {text-size:10; text-color:black;}  " +
            "edge.external {text-color:blue; text-size:14;}    " +
            "node.deadlock {fill-color: orange; text-color: red; size: 20px, 20px; text-size:15;}    " +
            "node.init { fill-color: green; text-color: black; size: 20px, 20px; text-size:15;}";
    protected static String STYLE_SHEET2 = "graph {  canvas-color: white; fill-mode: gradient-vertical; fill-color: white, #004; padding: 20px;  } " +
            "node { shape: circle; size-mode: dyn-size; size: 10px; fill-mode: gradient-radial; fill-color: #FFFC, #FFF0; stroke-mode: none; " +
            "shadow-mode: gradient-radial; shadow-color: #FFF5, #FFF0; shadow-width: 5px; shadow-offset: 0px, 0px; } " +
            "node: clicked { fill-color: #F00A, #F000;  } node: selected { fill-color: #00FA, #00F0;  } " +
            "edge { shape: angle; size: 1px; fill-color: red; fill-mode: plain; arrow-shape: circle; } " +
            "edge.defaultedge { shape: curve-cubic; size: 1px; fill-color: #FFF3; fill-mode: plain; arrow-shape: none; } " +
            "edge.external { shape: L-square-line; size: 3px; fill-color: #AAA3; fill-mode: plain; arrow-shape: circle; } " +
            "sprite { shape: circle; fill-mode: gradient-radial; fill-color: #FFF8, #FFF0; }";
    protected AUTGraph graph;
    protected SwingViewer viewer;
    protected MultiGraph vGraph;
    protected boolean loop;
    protected Node firstNode;
    protected ArrayList<Edge> edges;
    protected boolean exitOnClose = false;
    protected BasicFrame bf;

    // see http://graphstream-project.org/doc/Advanced-Concepts/GraphStream-CSS-Reference/
    protected boolean forceAutoLayout;
    protected String USED_STYLE_SHEET = "";


    public AUTGraphDisplay(AUTGraph _graph, boolean _exitOnClose, boolean _forceAutoLayout) {
        graph = _graph;
        exitOnClose = _exitOnClose;
        forceAutoLayout = _forceAutoLayout;
        System.setProperty("org.graphstream.ui", "swing");
    }


    public void display() {
        Node node;
        Edge edge;

        Logger l0 = Logger.getLogger("");
        try {
            if (l0 != null) {
                l0.removeHandler(l0.getHandlers()[0]);
            }
        } catch (Exception e) {
        }

        //System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        vGraph = new MultiGraph("TTool graph");
        if ((ConfigurationTTool.RGStyleSheet != null) && (ConfigurationTTool.RGStyleSheet.trim().length() > 0)) {
            TraceManager.addDev("Adding stylesheet:" + ConfigurationTTool.RGStyleSheet + "\n\nvs default:" + STYLE_SHEET);
            vGraph.setAttribute("ui.stylesheet", ConfigurationTTool.RGStyleSheet);
            USED_STYLE_SHEET = ConfigurationTTool.RGStyleSheet;
        } else {
            vGraph.setAttribute("ui.stylesheet", STYLE_SHEET);
            USED_STYLE_SHEET = STYLE_SHEET;
        }
        //vGraph.setAttribute("ui.stylesheet", STYLE_SHEET);


        //vGraph.addAttribute("layout.weight", 0.5);
        int cpt = 0;
        graph.computeStates();
        for (AUTState state : graph.getStates()) {
            node = vGraph.addNode("" + state.id);

            if (state.info == null) {
                node.setAttribute("ui.label", "" + state.id);
            } else {
                node.setAttribute("ui.label", "" + state.id + "/" + state.info);
            }

            if (state.getNbOutTransitions() == 0) {
                node.setAttribute("ui.class", "deadlock");
            }

            if ((cpt == 0) || (state.isOrigin)) {
                node.setAttribute("ui.class", "init");
                firstNode = node;
            }
            cpt++;
        }
        cpt = 0;
        //TraceManager.addDev("Here we are!");
        edges = new ArrayList<Edge>(graph.getTransitions().size());
        HashSet<AUTTransition> transitionsMet = new HashSet<>();
        for (AUTTransition transition : graph.getTransitions()) {
            edge = vGraph.addEdge("" + cpt, "" + transition.origin, "" + transition.destination, true);
            /*TraceManager.addDev("Transition=" + transition.transition);
              String tmp = Conversion.replaceAllChar(transition.transition, '(', "$");
              tmp = Conversion.replaceAllChar(tmp, ')', "$");
              TraceManager.addDev("Transition=" + tmp);*/
            edge.setAttribute("ui.label", graph.removeOTime(graph.removeSameSignal(graph.getCompoundString(transition, transitionsMet))));
            //edge.setAttribute("ui.style", "text-offset: -50, -50;");
            //edge.addAttribute("ui.class", "edge");
            //edge.addAttribute("shape", "cubic-curve");
            //edge.addAttribute("arrow-shape", "circle");
            edge.setAttribute("layout.weight", 0.4);
            if (!(transition.transition.startsWith("i("))) {
                edge.setAttribute("ui.class", "external");
            } else {
                edge.setAttribute("ui.class", "defaultedge");
            }
            edges.add(edge);
            cpt++;
        }
        //viewer = vGraph.display();
        //viewer = new Viewer(vGraph, Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD);

        viewer = new SwingViewer(vGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableXYZfeedback(true);


        //SwingUtilities.invokeLater(new InitializeApplication(viewer, vGraph));
        viewer.enableAutoLayout();
        //View   vi = viewer.addDefaultView(true);
        //vi.addMouseListener(this);


        //viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
        bf = new BasicFrame(this, viewer, vGraph, graph, edges, exitOnClose, forceAutoLayout);


        loop = true;

        Thread t = new Thread(this);
        t.start();

    }

    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        /*ViewerPipe fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(vGraph);*/


        // Then we need a loop to do our work and to wait for events.
        // In this loop we will need to call the
        // pump() method before each use of the graph to copy back events
        // that have already occurred in the viewer thread inside
        // our thread

        int cpt = 0;
        //ViewerPipe pipeIn = viewer.newViewerPipe();
        ProxyPipe pipe = viewer.newViewerPipe();
        pipe.addAttributeSink(vGraph);
        //pipeIn.addSink(vGraph);
        //pipeIn.addViewerListener(this);


        while (loop) {
            try {
                //TraceManager.addDev("pumping in first thread");
                Thread.sleep(100);
                pipe.pump();
            } catch (Exception e) {
                TraceManager.addDev("pumping exception: " + e.getMessage());
            }


            if (vGraph.hasAttribute("ui.viewClosed")) {
                //TraceManager.addDev("View was closed");
                loop = false;
                if (exitOnClose) {
                    System.exit(1);
                }
            }
            //TraceManager.addDev("bf.updateMe");
            if (bf != null)
                bf.updateMe();
        }

    }


    public void buttonPushed(String id) {
        TraceManager.addDev("Button pushed on node " + id);
    }

    public void buttonReleased(String id) {
        TraceManager.addDev("Button released on node " + id);
    }

    public void viewClosed(String id) {
        //TraceManager.addDev("View closed and closed !");
        loop = false;
        if (viewer != null) {
            viewer.close();
            viewer.disableAutoLayout();
        }
        viewer = null;
        vGraph.clear();
        if (exitOnClose) {
            System.exit(1);
        }
    }


    public void mousePressed(MouseEvent e) {
        TraceManager.addDev("Mouse pressed; # of clicks: "
                + e.getClickCount());
    }

    public void mouseReleased(MouseEvent e) {
        TraceManager.addDev("Mouse released; # of clicks: "
                + e.getClickCount());
    }

    public void mouseEntered(MouseEvent e) {
        TraceManager.addDev("Mouse entered");
    }

    public void mouseExited(MouseEvent e) {
        TraceManager.addDev("Mouse exited");
    }

    public void mouseClicked(MouseEvent e) {
        TraceManager.addDev("Mouse clicked (# of clicks: "
                + e.getClickCount() + ")");
    }


    public void mouseOver(java.lang.String id) {
        TraceManager.addDev("Mouse over: " + id);
    }

    public void mouseLeft(java.lang.String id) {
        TraceManager.addDev("Mouse over: " + id);
    }


    class BasicFrame extends JFrame implements ActionListener {
        public AffineTransform transform;
        protected MultiGraph vGraph;
        protected SwingViewer viewer;
        protected JPanel viewerPanel;
        protected AUTGraph graph;
        protected ArrayList<Edge> edges;
        protected JButton close;
        protected JButton screenshot;
        protected JButton fontPlus, fontMinus;
        protected JButton resetView;
        protected JCheckBox autoLayout;
        protected JCheckBox internalActions;
        protected JCheckBox readActions;
        protected JCheckBox higherQuality, antialiasing;
        protected JLabel help, info;
        protected MouseEvent last;

        protected boolean exitOnClose;

        private AUTGraphDisplay autD;

        private DefaultView dv;
        private Node selectedNode;

        private boolean forceAutoLayout;


        public BasicFrame(AUTGraphDisplay autD, SwingViewer viewer, MultiGraph vGraph, AUTGraph autgraph, ArrayList<Edge> _edges,
                          boolean _exitOnClose, boolean _forceAutoLayout) {
            this.autD = autD;
            this.viewer = viewer;
            this.vGraph = vGraph;
            this.graph = autgraph;
            forceAutoLayout = _forceAutoLayout;


            edges = _edges;
            exitOnClose = _exitOnClose;
            makeComponents();

            if (exitOnClose) {
                setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            }

        }


        public void processDrag(MouseEvent event) {
        /*if (last != null) {
            Camera camera = dv.getCamera();
            Point3 p1 = camera.getViewCenter();
            Point3 p2 = camera.transformGuToPx(p1.x,p1.y,0);
            int xdelta=event.getX()-last.getX();//determine direction
            int ydelta=event.getY()-last.getY();//determine direction

            p2.x-=xdelta;
            p2.y-=ydelta;
            Point3 p3= camera.transformPxToGu(p2.x,p2.y);
            camera.setViewCenter(p3.x,p3.y, 0);
        }
        last=event;*/
        }

        public void resetDrag() {
            //this.last=null;
        }


        public void makeComponents() {


            dv = new DefaultView(viewer, "Graph", new SwingGraphRenderer());

            MouseListener[] listeners = dv.getMouseListeners();
            for (MouseListener l : listeners) {
                dv.removeMouseListener(l);
            }

            MouseMotionListener[] mL = dv.getMouseMotionListeners();
            for (MouseMotionListener l : mL) {
                dv.removeMouseMotionListener(l);
            }

            KeyListener[] keyListeners = dv.getKeyListeners();
            for (KeyListener k : keyListeners) {
                dv.removeKeyListener(k);
            }



            /*((Component) dv).addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    TraceManager.addDev("Key pressed event:" + e.getKeyCode());
                    updateMe();
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    TraceManager.addDev("Key pressed event:" + e.getKeyCode());
                    updateMe();
                }

                @Override
                public void keyReleased(KeyEvent e) {

                    TraceManager.addDev("Key event:" + e.getKeyCode());
                    updateMe();
                }
            });*/


            dv.addMouseMotionListener(new MouseMotionListener() {

                @Override
                public void mouseDragged(MouseEvent e) {
                    //TraceManager.addDev("Dragged!");
                    if (selectedNode != null) {
                        double diffX = 1;
                        double diffY = 1;
                        if (transform != null) {
                            diffX = transform.getScaleX();
                            diffY = transform.getScaleY();
                        }
                        Point3 p = dv.getCamera().transformPxToGu(e.getX() * diffX, e.getY() * diffY);
                        selectedNode.setAttribute("x", p.x);
                        selectedNode.setAttribute("y", p.y);
                    }
                    processDrag(e);
                }

                @Override
                public void mouseMoved(MouseEvent e) {

                }
            });

            dv.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    //TraceManager.addDev("Mouse clicked!");
                }

                @Override
                public void mousePressed(MouseEvent e) {


                    Graphics2D g = (Graphics2D) dv.getGraphics();
                    transform = g.getFontRenderContext().getTransform();
                    //TraceManager.addDev("Affine transform = " + transform);


                    //setAutoLayout(false);
                    //viewer.disableAutoLayout();
                    //TraceManager.addDev("Mouse pressed!");

                    Node node = findNodeAt(e.getX(), e.getY());
                    if (node != null) {
                        //TraceManager.addDev("Mouse pressed at node: " + node.getId());
                        selectedNode = node;
                    } else {
                        //TraceManager.addDev("Mouse pressed not on a node");
                    }

                    resetDrag();

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    //TraceManager.addDev("Mouse released!");
                    selectedNode = null;
                    //viewer.enableAutoLayout();
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });


            /*((Component) dv).addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    e.consume();
                    int i = e.getWheelRotation();
                    TraceManager.addDev("Wheel rotation:" + i);
                    double factor = Math.pow(1.02, i);
                    Camera cam = dv.getCamera();
                    double zoom = cam.getViewPercent() * factor;
                Point2 pxCenter  = cam.transformGuToPx(cam.getViewCenter().x, cam.getViewCenter().y, 0);
                double diffX = 1;
                double diffY = 1;
                if (transform != null) {
                    diffX = transform.getScaleX();
                    diffY = transform.getScaleY();
                }
                Point3 guClicked = cam.transformPxToGu(e.getX()*diffX, e.getY()*diffY);

                double newRatioPx2Gu = cam.getMetrics().ratioPx2Gu/factor;
                double x = guClicked.x + (pxCenter.x - e.getX())/newRatioPx2Gu;
                double y = guClicked.y - (pxCenter.y - e.getY())/newRatioPx2Gu;
                cam.setViewCenter(x, y, 0);
                    cam.setViewPercent(zoom);
                }
            });*/


            //Graphics g = dv.getGraphics();
            //TraceManager.addDev("Transform:" + ((Graphics2D)g).getTransform());
            //TraceManager.addDev("scale:" + System.getProperty("sun.java2d.uiScale"));


            add(dv, BorderLayout.CENTER);

            //add(viewer, BorderLayout.CENTER );
            close = new JButton("Close", IconManager.imgic27);
            close.addActionListener(this);
            screenshot = new JButton("Save in png", IconManager.imgic28);
            screenshot.addActionListener(this);
            fontPlus = new JButton(IconManager.imgic144);
            fontPlus.addActionListener(this);
            fontMinus = new JButton(IconManager.imgic146);
            fontMinus.addActionListener(this);
            resetView = new JButton("Reset view");
            resetView.addActionListener(this);
            autoLayout = new JCheckBox("Auto layout", true);
            autoLayout.addActionListener(this);
            close.addActionListener(this);
            help = new JLabel("Zoom with mouse wheel or PageUp/PageDown, move with cursor keys");
            help = new JLabel("click on a node and drag it to move it");
            info = new JLabel("Graph: " + graph.getNbOfStates() + " states, " + graph.getNbOfTransitions() + " transitions");
            internalActions = new JCheckBox("Display internal actions", true);
            internalActions.addActionListener(this);
            readActions = new JCheckBox("Display read/write actions", true);
            readActions.addActionListener(this);
            higherQuality = new JCheckBox("Higher drawing quality", false);
            higherQuality.addActionListener(this);
            antialiasing = new JCheckBox("Anti aliasing", false);
            antialiasing.addActionListener(this);


            JPanel jp01 = new JPanel();
            GridBagLayout gridbag01 = new GridBagLayout();
            GridBagConstraints c01 = new GridBagConstraints();
            jp01.setLayout(gridbag01);
            jp01.setBorder(new javax.swing.border.TitledBorder("Options"));
            //c01.gridwidth = 1;
            c01.gridheight = 1;
            c01.weighty = 1.0;
            c01.weightx = 1.0;
            c01.fill = GridBagConstraints.HORIZONTAL;
            c01.gridwidth = GridBagConstraints.REMAINDER; //end row
            jp01.add(screenshot);
            jp01.add(internalActions);
            jp01.add(readActions);
            jp01.add(higherQuality);
            jp01.add(antialiasing);
            if (!forceAutoLayout) {
                jp01.add(autoLayout);
            }
            jp01.add(fontMinus);
            jp01.add(fontPlus);
            jp01.add(resetView);

            JPanel infoPanel = new JPanel(new BorderLayout());
            JPanel labelPanel = new JPanel(new BorderLayout());
            labelPanel.add(help, BorderLayout.EAST);
            labelPanel.add(info, BorderLayout.WEST);
            infoPanel.add(labelPanel, BorderLayout.NORTH);
            infoPanel.add(close, BorderLayout.SOUTH);
            infoPanel.add(jp01, BorderLayout.CENTER);


            add(infoPanel, BorderLayout.SOUTH);
            //setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(1000, 700);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == close) {
                closeFrame();
            } else if (evt.getSource() == screenshot) {
                screenshot();
            } else if (evt.getSource() == internalActions) {
                manageInternalActions();
            } else if (evt.getSource() == readActions) {
                manageReadActions();
            } else if (evt.getSource() == higherQuality) {
                manageHigherQuality();
            } else if (evt.getSource() == antialiasing) {
                manageAntialiasing();
            } else if (evt.getSource() == fontPlus) {
                fontPlus();
            } else if (evt.getSource() == fontMinus) {
                fontMinus();
            } else if (evt.getSource() == resetView) {
                dv.getCamera().resetView();
                updateMe();
            } else if (evt.getSource() == autoLayout) {
                if (autoLayout.isSelected()) {
                    viewer.enableAutoLayout();
                } else {
                    viewer.disableAutoLayout();
                }
            }
        }

        public void closeFrame() {
            if (autD != null) {
                autD.viewClosed("closed pushed");
            }
            if (exitOnClose) {
                System.exit(1);
            }
            dispose();
        }

        public void takeScreenshot(Component component, File file) {
            BufferedImage image = new BufferedImage(
                    component.getWidth(),
                    component.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );
            // call the Component's paint method, using
            // the Graphics object of the image.
            component.paint(image.getGraphics()); // alternately use .printAll(..)

            try {
                // save captured image to PNG file
                ImageIO.write(image, "png", file);
            } catch (Exception e) {
            }

        }

        public void fontPlus() {
            setTextSize(1);

        }

        public void fontMinus() {
            setTextSize(-1);
        }

        private void setTextSize(int modifier) {
            String style = USED_STYLE_SHEET;
            if ((style == null) || (style.length() == 0)) {
                return;
            }

            // default.edge
            //TraceManager.addDev("Old style: " + style);
            style = modifyTextSize(modifier, style, "node ");
            style = modifyTextSize(modifier, style, "node.deadlock");
            style = modifyTextSize(modifier, style, "node.init");
            style = modifyTextSize(modifier, style, "edge ");
            style = modifyTextSize(modifier, style, "edge.defaultedge");
            //TraceManager.addDev("New style default: " + style);
            // external
            style = modifyTextSize(modifier, style, "edge.external");
            //TraceManager.addDev("New style external: " + style);
            vGraph.setAttribute("ui.stylesheet", style);
            USED_STYLE_SHEET = style;


        }

        public String modifyTextSize(int modifier, String input, String key) {
            String ret = input;
            int indexD = input.indexOf(key);
            if (indexD != -1) {
                String before = input.substring(0, indexD);

                String tmp = input.substring(indexD);
                int indexBB = tmp.indexOf("{");
                int indexBE = tmp.indexOf("}");
                int indexT = tmp.indexOf("text-size:");
                if ((indexT > -1) && (indexBB > -1) && (indexBE > -1) && (indexT > indexBB) && (indexT < indexBE)) {
                    String bef = tmp.substring(0, indexT + 10);
                    String textSize = tmp.substring(indexT + 10);
                    int indexVirg = textSize.indexOf(";");
                    if (indexVirg > -1) {
                        String textValue = textSize.substring(0, indexVirg);
                        String endf = textSize.substring(indexVirg);
                        int val = Integer.decode(textValue);
                        val += modifier;
                        if (val < 1) {
                            val = 1;
                        }
                        //TraceManager.addDev("Value=" + val);
                        // Rebuild the string
                        ret = before + bef + val + endf;
                    }
                }

            }
            return ret;
        }

        public void screenshot() {
            TraceManager.addDev("Screenshot");
            JFileChooser jfcggraph;
            if (SpecConfigTTool.GGraphPath.length() > 0) {
                jfcggraph = new JFileChooser(SpecConfigTTool.GGraphPath);
            } else {
                jfcggraph = new JFileChooser();
            }
            PNGFilter filter = new PNGFilter();
            jfcggraph.setFileFilter(filter);
            int returnVal = jfcggraph.showDialog(this, "Graph capture (in png)");
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File pngFile = jfcggraph.getSelectedFile();
            TraceManager.addDev("Making the screenshot in " + pngFile.getAbsolutePath());

            takeScreenshot(dv, pngFile);

            TraceManager.addDev("Screenshot performed");
        }


        public synchronized void manageInternalActions() {
            if (edges == null) {
                return;
            }
            int cpt = 0;
            for (AUTTransition transition : graph.getTransitions()) {
                if (transition.transition.startsWith("i(")) {
                    if (internalActions.isSelected()) {
                        edges.get(cpt).setAttribute("ui.label", transition.transition);
                    } else {
                        edges.get(cpt).setAttribute("ui.label", "");
                    }
                }
                cpt++;
            }
            //dv.repaint();
        }

        public synchronized void manageReadActions() {
            if (edges == null) {
                return;
            }
            int cpt = 0;
            for (AUTTransition transition : graph.getTransitions()) {
                if (transition.transition.contains("?")) {
                    if (readActions.isSelected()) {
                        edges.get(cpt).setAttribute("ui.label", transition.transition);
                    } else {
                        edges.get(cpt).setAttribute("ui.label", "");
                    }
                }
                cpt++;
            }
            //dv.repaint();
        }

        public synchronized void manageHigherQuality() {
            //viewer.disableAutoLayout();
            if (higherQuality.isSelected()) {
                vGraph.setAttribute("ui.quality");
            } else {
                vGraph.removeAttribute("ui.quality");
            }
            try {
                //viewer.enableAutoLayout();
                //dv.repaint();
            } catch (Exception e) {
            }
        }


        public synchronized void manageAntialiasing() {
            //viewer.disableAutoLayout();
            if (antialiasing.isSelected()) {
                vGraph.setAttribute("ui.antialias");
            } else {
                vGraph.removeAttribute("ui.antialias");
            }
            try {
                //viewer.enableAutoLayout();
                //dv.repaint();
            } catch (Exception e) {
            }
        }

        public synchronized void updateMe() {
            //TraceManager.addDev("updateMe");
            dv.repaint();
        }

        public Node findNodeAt(int x, int y) {
            for (int i = 0; i < vGraph.getNodeCount(); i++) {
                //TraceManager.addDev("index:" + i);
                Node node = vGraph.getNode(i);
                if (node != null) {
                    double pos[] = Toolkit.nodePosition(node);
                    if (pos != null) {
                        //TraceManager.addDev("pos of Node " + node.getId() + " is " + pos[0] + "," + pos[1]);
                        Point3 position = dv.getCamera().transformGuToPx(pos[0], pos[1], 0);
                        //TraceManager.addDev("Coordinate = " + position + " vs mouse : " + x + "," + y);
                        double diffX = 1;
                        double diffY = 1;
                        if (transform != null) {
                            diffX = transform.getScaleX();
                            diffY = transform.getScaleY();
                        }
                        if ((Math.abs(position.x - diffX * x) < 10) && (Math.abs(position.y - diffY * y) < 10)) {
                            return node;
                        }
                    } else {
                        TraceManager.addDev("Null pos");
                    }
                }
            }

            return null;
        }

    /*public void setAutoLayout(boolean auto) {
        autoLayout.setSelected(auto);
    }*/


    } // Basic Frame


} // Main class
