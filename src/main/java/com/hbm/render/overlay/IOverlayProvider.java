package com.hbm.render.overlay;

import java.util.List;

public interface IOverlayProvider {
    boolean shouldRender(OverlayContext context);
    List<OverlaySection> getSections(OverlayContext context);
}