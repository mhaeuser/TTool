package ui;

public abstract class TGScalableComponent extends TGComponent implements ScalableTGComponent {
	
	protected boolean rescaled;
	
	protected double oldScaleFactor;

	public TGScalableComponent(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos,
			TGComponent _father, TDiagramPanel _tdp) {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
		
		rescaled = false;
        oldScaleFactor = tdp.getZoom();//1.0;
	}

    protected void initScaling(int w, int h) {
        oldScaleFactor = tdp.getZoom();

        dx = 0;
        dy = 0;

        dwidth = w * oldScaleFactor;
        width = (int)dwidth;
        dwidth = dwidth - width;

        dheight = h * oldScaleFactor;
        height = (int)(dheight);
        dheight = dheight - height;

        dMaxWidth = defMaxWidth * oldScaleFactor;
        dMaxHeight = defMaxHeight * oldScaleFactor;

        maxWidth = defMaxWidth;
        maxHeight = defMaxHeight;

        dMaxWidth = dMaxWidth - maxWidth;
        dMaxHeight = dMaxHeight - maxHeight;

        if (father == null) {
            minX = (int)(tdp.getMinX()/tdp.getZoom());
            maxX = (int)(tdp.getMaxX()/tdp.getZoom());
            minY = (int)(tdp.getMinY()/tdp.getZoom());
            maxY = (int)(tdp.getMaxY()/tdp.getZoom());
        }

        rescaled = true;
    }

    @Override
    public void rescale( final double scaleFactor ) {
        rescaled = true;
        
        final double factor = scaleFactor / oldScaleFactor;

        dwidth = (width + dwidth) * factor;
        dheight = (height + dheight) * factor;
        dx = (dx + x) * factor;
        dy = (dy + y) * factor;
        dMinWidth = (minWidth + dMinWidth) * factor;
        dMinHeight = (minHeight + dMinHeight) * factor;//oldScaleFactor * scaleFactor;
        dMaxWidth = (maxWidth + dMaxWidth) * factor;//oldScaleFactor * scaleFactor;
        dMaxHeight = (maxHeight + dMaxHeight) * factor;//oldScaleFactor * scaleFactor;

        width = (int)(dwidth);
        dwidth = dwidth - width;
        height = (int)(dheight);
        dheight = dheight - height;
        minWidth = (int)(dMinWidth);
        minHeight = (int)(dMinHeight);
        maxWidth = (int)(dMaxWidth);
        maxHeight = (int)(dMaxHeight);

        dMinWidth = dMinWidth - minWidth;
        dMinHeight = dMinHeight - minHeight;
        dMaxWidth = dMaxWidth - maxWidth;
        dMaxHeight = dMaxHeight - maxHeight;
        x = (int)(dx);
        dx = dx - x;
        y = (int)(dy);
        dy = dy - y;
        
        // Issue #81: We also need to update max coordinate values
        maxX *= factor;
        maxY *= factor;

        oldScaleFactor = scaleFactor;

        if (father != null) {
            // Must rescale my zone...
            resizeWithFather();
        } else {
            minX = (int)(tdp.getMinX()/tdp.getZoom());
            maxX = (int)(tdp.getMaxX()/tdp.getZoom());
            minY = (int)(tdp.getMinY()/tdp.getZoom());
            maxY = (int)(tdp.getMaxY()/tdp.getZoom());
        }

        setMoveCd(x, y, true);

        //TraceManager.addDev("x=" + x + " y=" + y + " width=" + width + " height=" + height);

        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof ScalableTGComponent) {
                ((ScalableTGComponent)tgcomponent[i]).rescale(scaleFactor);
            }
        }
        
        hasBeenResized();
    }
}
