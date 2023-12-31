
#include <GL/vgafb.h>
#include <GL/gl.h>

/* for GLContext definition */
#include "zgl.h"

struct vgafb_context {
    GLContext *gl_context;
    struct device_s *fb_dev;
};

static int vgafb_resize_viewport(GLContext *ctx, int *xsize, int *ysize);

error_t vgafb_create_context(struct vgafb_context **ctx )
{
    struct vgafb_context *c;

    if ((c = (struct vgafb_context*)gl_malloc(sizeof(struct vgafb_context))) == NULL)
        return 1;

    c->gl_context = NULL;

    *ctx = c;
    return 0;
}

error_t vgafb_destroy_context(struct vgafb_context *ctx)
{
    if (ctx->gl_context != NULL)
        glClose();

    free(ctx);

    return 0;
}

/* connect a framebuffer device to a context */
error_t vgafb_make_current(struct vgafb_context *ctx, struct device_s *fb_dev)
{

    if (ctx->gl_context == NULL)
    {
        int i;

        /* Open a ZBuffer
         *  (320x200 pixels, 8 bits color index with palette) */
        ZBuffer *zb;

        unsigned char color_indexes[ZB_NB_COLORS];
        int palette[ZB_NB_COLORS];

        /* values correspond to indexes */
        for (i=0; i<ZB_NB_COLORS; i++)
            color_indexes[i]=i;

        if ((zb = ZB_open(320, 200, ZB_MODE_INDEX, ZB_NB_COLORS, color_indexes, palette, NULL)) == NULL)
            return 1;

        /* set the framebuffer palette
         *  (with the palette returned by ZB) */
        struct fb_pal_s pal[ZB_NB_COLORS];
        for (i=0; i<ZB_NB_COLORS; i++)
        {
            pal[i].r = (palette[i]>>16) & 0xFF;
            pal[i].g = (palette[i]>>8)  & 0xFF;
            pal[i].b = (palette[i])     & 0xFF;
        }
        dev_fb_setpalette (fb_dev, pal, ZB_NB_COLORS);

        /* Init TinyGL interpreter */
        glInit(zb);
        ctx->gl_context = gl_get_context();
        ctx->gl_context->opaque = (void*)ctx;

        /* set the viewport
         *  we do not provide this now, since we can't resize */
        //ctx->gl_context->gl_resize_viewport = vgafb_resize_viewport;
        //ctx->gl_context->viewport.xsize = -1;
        //ctx->gl_context->viewport.ysize = -1;
        //glViewport(0, 0, 320, 200);
    }

    ctx->fb_dev = fb_dev;

    return 0;
}

error_t vgafb_swap_buffer()
{
    GLContext *gl_context;
    struct vgafb_context *ctx;

    /* retrieve current vgafb_context */
    gl_context = gl_get_context();
    ctx = (struct vgafb_context*)gl_context->opaque;

    /* get framebuffer ram address */
    char *fb;
    fb = (char*)dev_fb_getbuffer(ctx->fb_dev, 0);

    /* Update framebuffer (the linesize is xsize*2 since tinygl works on 16 bits values */
    int linesize = gl_context->zb->xsize*2;
    ZB_copyFrameBuffer(gl_context->zb, fb, linesize);

    return 0;
}

static int vgafb_resize_viewport(GLContext *ctx, int *xsize, int *ysize)
{
    /* we can authorize only these values */
    *xsize = 320;
    *ysize = 200;

    return 0;
}
