package suzaku.ui

trait ComponentBlueprint extends Blueprint {
  def create: StateProxy => Component[_, _]

  def sameAs(that: this.type): Boolean = equals(that)
}
