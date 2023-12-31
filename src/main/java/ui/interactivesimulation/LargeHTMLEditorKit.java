package ui.interactivesimulation;

import javax.swing.text.*;
import javax.swing.text.html.BlockView;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class LargeHTMLEditorKit extends HTMLEditorKit {

    ViewFactory factory = new MyViewFactory();

    @Override
    public ViewFactory getViewFactory() {
        return factory;
    }

    class MyViewFactory extends HTMLFactory {
        @Override
        public View create(Element elem) {
            AttributeSet attrs = elem.getAttributes();
            Object elementName = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
            Object o = (elementName != null) ? null : attrs.getAttribute(StyleConstants.NameAttribute);
            if (o instanceof HTML.Tag) {
                HTML.Tag kind = (HTML.Tag) o;
                if (kind == HTML.Tag.HTML) {
                    return new HTMLBlockView(elem);
                }
                else if (kind == HTML.Tag.IMPLIED) {
                    String ws = (String) elem.getAttributes().getAttribute(CSS.Attribute.WHITE_SPACE);
                    if ((ws != null) && ws.equals("pre")) {
                        return super.create(elem);
                    }
                    return new HTMLParagraphView(elem);
                } else if ((kind == HTML.Tag.P) ||
                        (kind == HTML.Tag.H1) ||
                        (kind == HTML.Tag.H2) ||
                        (kind == HTML.Tag.H3) ||
                        (kind == HTML.Tag.H4) ||
                        (kind == HTML.Tag.H5) ||
                        (kind == HTML.Tag.H6) ||
                        (kind == HTML.Tag.DT)) {
                    // paragraph
                    return new HTMLParagraphView(elem);
                }
            }
            return super.create(elem);
        }

    }


    private class HTMLBlockView extends BlockView {

        public HTMLBlockView(Element elem) {
            super(elem,  View.Y_AXIS);
        }

        @Override
        protected void layout(int width, int height) {
            if (width<Integer.MAX_VALUE) {
                super.layout((int)(width / getZoomFactor()),
                        (int)(height * getZoomFactor()));
            }
        }

        public double getZoomFactor() {
            Double scale = (Double) getDocument().getProperty("ZOOM_FACTOR");
            if (scale != null) {
                return scale;
            }

            return 1;
        }

        @Override
        public void paint(Graphics g, Shape allocation) {
            Graphics2D g2d = (Graphics2D) g;
            double zoomFactor = getZoomFactor();
            AffineTransform old = g2d.getTransform();
            g2d.scale(zoomFactor, zoomFactor);
            super.paint(g2d, allocation);
            g2d.setTransform(old);
        }

        @Override
        public float getMinimumSpan(int axis) {
            float f = super.getMinimumSpan(axis);
            f *= getZoomFactor();
            return f;
        }

        @Override
        public float getMaximumSpan(int axis) {
            float f = super.getMaximumSpan(axis);
            f *= getZoomFactor();
            return f;
        }

        @Override
        public float getPreferredSpan(int axis) {
            float f = super.getPreferredSpan(axis);
            f *= getZoomFactor();
            return f;
        }

        @Override
        public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
            double zoomFactor = getZoomFactor();
            Rectangle alloc;
            alloc = a.getBounds();
            Shape s = super.modelToView(pos, alloc, b);
            alloc = s.getBounds();
            alloc.x *= zoomFactor;
            alloc.y *= zoomFactor;
            alloc.width *= zoomFactor;
            alloc.height *= zoomFactor;

            return alloc;
        }

        @Override
        public int viewToModel(float x, float y, Shape a,
                               Position.Bias[] bias) {
            double zoomFactor = getZoomFactor();
            Rectangle alloc = a.getBounds();
            x /= zoomFactor;
            y /= zoomFactor;
            alloc.x /= zoomFactor;
            alloc.y /= zoomFactor;
            alloc.width /= zoomFactor;
            alloc.height /= zoomFactor;

            return super.viewToModel(x, y, alloc, bias);
        }

    }
    class HTMLParagraphView extends ParagraphView {

        public int MAX_VIEW_SIZE=100;

        public HTMLParagraphView(Element elem) {
            super(elem);
            strategy = new HTMLParagraphView.HTMLFlowStrategy();
        }

        public class HTMLFlowStrategy extends FlowStrategy {
            protected View createView(FlowView fv, int startOffset, int spanLeft, int rowIndex) {
                View res=super.createView(fv, startOffset, spanLeft, rowIndex);
                if (res.getEndOffset()-res.getStartOffset()> MAX_VIEW_SIZE) {
                    res = res.createFragment(startOffset, startOffset+ MAX_VIEW_SIZE);
                }
                return res;
            }

        }
        public int getResizeWeight(int axis) {
            return 0;
        }
    }
}