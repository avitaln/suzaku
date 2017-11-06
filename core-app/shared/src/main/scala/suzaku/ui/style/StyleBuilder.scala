package suzaku.ui.style

import suzaku.ui.{UIManagerProxy, WidgetProtocolProvider}

class StyleBuilder[S <: StyleProperty, V](build: V => S) {
  def :=(value: V): S = build(value)
}

class MultiStyleBuilder[S <: StyleProperty, V](build: List[V] => S) {
  def :=(value: V, values: V*): S = build(value :: values.toList)
}

abstract class TRBLBuilder[A](top: A => StyleBaseProperty,
                              right: A => StyleBaseProperty,
                              bottom: A => StyleBaseProperty,
                              left: A => StyleBaseProperty) {

  def :=(all: A) = StyleSeq(top(all), right(all), bottom(all), left(all))

  def :=(tb: A, lr: A) = StyleSeq(top(tb), right(lr), bottom(tb), left(lr))

  def :=(t: A, lr: A, b: A) = StyleSeq(top(t), right(lr), bottom(b), left(lr))

  def :=(t: A, r: A, b: A, l: A) = StyleSeq(top(t), right(r), bottom(b), left(l))
}

trait StyleBuilders {
  def styleFor[S <: StyleProperty, V](build: V => S)        = new StyleBuilder(build)
  def stylesFor[S <: StyleProperty, V](build: List[V] => S) = new MultiStyleBuilder(build)

  def buildWidgetStyles(styleMapping: (WidgetProtocolProvider, List[StyleClass])*): WidgetStyles = {
    val mapping: Map[Int, List[StyleClass]] = styleMapping.map {
      case (widget, styles) => (UIManagerProxy.getWidgetClass(widget.widgetProtocol), styles)
    }(collection.breakOut)
    WidgetStyles(mapping)
  }

  // for style classes
  val inheritClass   = styleFor[InheritClasses, StyleClass](styleClass => InheritClasses(styleClass :: Nil))
  val inheritClasses = stylesFor[InheritClasses, StyleClass](InheritClasses)
  val extendClass    = styleFor[ExtendClasses, StyleClass](styleClass => ExtendClasses(styleClass :: Nil))
  val extendClasses  = stylesFor[ExtendClasses, StyleClass](ExtendClasses)
  val remapClass     = styleFor[RemapClasses, (StyleClass, List[StyleClass])](ct => RemapClasses(Map(ct._1 -> ct._2)))
  val remapClasses   = stylesFor[RemapClasses, (StyleClass, List[StyleClass])](ct => RemapClasses(ct.toMap))
  val widgetStyle =
    styleFor[WidgetStyles, (WidgetProtocolProvider, List[StyleClass])](ct => buildWidgetStyles(ct._1 -> ct._2))
  val widgetStyles =
    stylesFor[WidgetStyles, (WidgetProtocolProvider, List[StyleClass])](ct => buildWidgetStyles(ct: _*))

  // raw CSS
  val cssClasses = stylesFor(RawCSSClasses)
  val cssStyle   = stylesFor(RawCSSStyles)

  // color style definitions
  val color           = styleFor(ForegroundColor)
  val backgroundColor = styleFor(BackgroundColor)
  val fromPalette     = styleFor(FromPalette)

  // font
  val fontFamily = stylesFor[FontFamily, String](families => FontFamily(families))
  val fontSize   = styleFor(FontSize)
  val fontWeight = styleFor(FontWeight)

  // layout styles
  val marginLeft   = styleFor(MarginLeft)
  val marginTop    = styleFor(MarginTop)
  val marginRight  = styleFor(MarginRight)
  val marginBottom = styleFor(MarginBottom)
  class MarginBuilder extends TRBLBuilder[LengthDimension](MarginTop, MarginRight, MarginBottom, MarginLeft)
  val margin = new MarginBuilder

  val paddingLeft   = styleFor(PaddingLeft)
  val paddingTop    = styleFor(PaddingTop)
  val paddingRight  = styleFor(PaddingRight)
  val paddingBottom = styleFor(PaddingBottom)
  class PaddingBuilder extends TRBLBuilder[LengthDimension](PaddingTop, PaddingRight, PaddingBottom, PaddingLeft)
  val padding = new PaddingBuilder

  val offsetLeft   = styleFor(OffsetLeft)
  val offsetTop    = styleFor(OffsetTop)
  val offsetRight  = styleFor(OffsetRight)
  val offsetBottom = styleFor(OffsetBottom)
  class OffsetBuilder extends TRBLBuilder[LengthDimension](OffsetTop, OffsetRight, OffsetBottom, OffsetLeft)
  val offset = new OffsetBuilder

  val borderWidthLeft   = styleFor(BorderWidthLeft)
  val borderWidthTop    = styleFor(BorderWidthTop)
  val borderWidthRight  = styleFor(BorderWidthRight)
  val borderWidthBottom = styleFor(BorderWidthBottom)
  class BorderWidthBuilder
      extends TRBLBuilder[WidthDimension](BorderWidthTop, BorderWidthRight, BorderWidthBottom, BorderWidthLeft)
  val borderWidth = new BorderWidthBuilder

  val borderStyleLeft   = styleFor(BorderStyleLeft)
  val borderStyleTop    = styleFor(BorderStyleTop)
  val borderStyleRight  = styleFor(BorderStyleRight)
  val borderStyleBottom = styleFor(BorderStyleBottom)
  class BorderStyleBuilder
      extends TRBLBuilder[LineStyle](BorderStyleTop, BorderStyleRight, BorderStyleBottom, BorderStyleLeft)
  val borderStyle = new BorderStyleBuilder

  val borderColorLeft   = styleFor(BorderColorLeft)
  val borderColorTop    = styleFor(BorderColorTop)
  val borderColorRight  = styleFor(BorderColorRight)
  val borderColorBottom = styleFor(BorderColorBottom)
  class BorderColorBuilder extends TRBLBuilder[Color](BorderColorTop, BorderColorRight, BorderColorBottom, BorderColorLeft)
  val borderColor = new BorderColorBuilder
  class BorderBuilder {
    def :=(w: WidthDimension)                         = borderWidth := w
    def :=(w: WidthDimension, s: LineStyle)           = (borderWidth := w) ++ (borderStyle := s)
    def :=(w: WidthDimension, s: LineStyle, c: Color) = (borderWidth := w) ++ (borderStyle := s) ++ (borderColor := c)
  }
  val border = new BorderBuilder

  val outlineWidth = styleFor(OutlineWidth)
  val outlineStyle = styleFor(OutlineStyle)
  val outlineColor = styleFor(OutlineColor)

  class OutlineBuilder {
    def :=(w: WidthDimension)                         = outlineWidth := w
    def :=(w: WidthDimension, s: LineStyle)           = StyleSeq(outlineWidth := w, outlineStyle := s)
    def :=(w: WidthDimension, s: LineStyle, c: Color) = StyleSeq(outlineWidth := w, outlineStyle := s, outlineColor := c)
  }
  val outline = new OutlineBuilder

  val tableLayout = styleFor(TableLayout)

  // layout and dimensions
  val width      = styleFor(Width)
  val height     = styleFor(Height)
  val maxWidth   = styleFor(MaxWidth)
  val maxHeight  = styleFor(MaxHeight)
  val minWidth   = styleFor(MinWidth)
  val minHeight  = styleFor(MinHeight)
  val gridRowGap = styleFor(GridRowGap)
  val gridColGap = styleFor(GridColGap)

  // pseudo classes
  val hover                        = stylesFor[Hover, StyleBaseProperty](Hover)
  val active                       = stylesFor[Active, StyleBaseProperty](Active)
  def nthChild(a: Int, b: Int = 0) = stylesFor[NthChild, StyleBaseProperty](props => NthChild(a, b, props))
}
