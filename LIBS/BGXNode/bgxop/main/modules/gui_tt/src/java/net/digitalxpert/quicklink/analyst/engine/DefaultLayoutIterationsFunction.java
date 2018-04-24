package net.bgx.bgxnetwork.bgxop.engine;

/**
 * Class DefaultLayoutIterationsFunction
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class DefaultLayoutIterationsFunction implements ILayoutIterationsFunction {
  private int frIterations = 50;
  private int kkIterations = 600;
  private int isomIterations = 100;
  private int springIterations = 200;

  public DefaultLayoutIterationsFunction() {
  }

  public int getIterations(LayoutType layoutType) {
    switch (layoutType) {
      case Circle:
        return 0;
      case FR:
        return frIterations;
      case ISOM:
        return isomIterations;
      case KK:
        return kkIterations;
      case Spring:
        return springIterations;
      case Static:
        return 0;
      default:
        return 0;
    }
  }

  public int getFrIterations() {
    return frIterations;
  }

  public void setFrIterations(int frIterations) {
    this.frIterations = frIterations;
  }

  public int getKkIterations() {
    return kkIterations;
  }

  public void setKkIterations(int kkIterations) {
    this.kkIterations = kkIterations;
  }

  public int getIsomIterations() {
    return isomIterations;
  }

  public void setIsomIterations(int isomIterations) {
    this.isomIterations = isomIterations;
  }

  public int getSpringIterations() {
    return springIterations;
  }

  public void setSpringIterations(int springIterations) {
    this.springIterations = springIterations;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final DefaultLayoutIterationsFunction that = (DefaultLayoutIterationsFunction) o;

    if (frIterations != that.frIterations) return false;
    if (isomIterations != that.isomIterations) return false;
    if (kkIterations != that.kkIterations) return false;
    if (springIterations != that.springIterations) return false;

    return true;
  }

  public int hashCode() {
    int result;
    result = frIterations;
    result = 29 * result + kkIterations;
    result = 29 * result + isomIterations;
    result = 29 * result + springIterations;
    return result;
  }
}
