package suzaku.ui.layout

class LayoutBuilder[S <: LayoutProperty, V](build: V => S) {
  def :=(value: V): S = build(value)
}

trait LayoutBuilders {
  def layoutFor[S <: LayoutProperty, V](build: V => S) = new LayoutBuilder(build)

  val alignSelf   = layoutFor(AlignSelf)
  val justifySelf = layoutFor(JustifySelf)
  val order       = layoutFor(Order)
  val zOrder      = layoutFor(ZOrder)
  val slot        = layoutFor(LayoutSlotId)
  val weight      = layoutFor(LayoutWeight)
}
