/**
 * Copyright 2007 Wei-ju Wu
 *
 * This file is part of TinyUML.
 *
 * TinyUML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * TinyUML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TinyUML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.tinyuml.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.regex.Pattern;

/**
 * This class represents a range to represent UML multiplicities. Multiplicity
 * is a simple data value holder and therefore made immutable.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class Multiplicity implements Serializable {

  private static final long serialVersionUID = -8450628964473567522L;
  private int lowerBound = 1, upperBound = 1;
  private boolean noUpperLimit = false;

  // Default multiplicities which are used most frequently
  public static final Multiplicity ZERO_TO_ONE =
    Multiplicity.getBoundedInstance(0, 1);
  public static final Multiplicity ONE = new Multiplicity();
  public static final Multiplicity N = Multiplicity.getUnboundedInstance(0);
  public static final Multiplicity ONE_TO_N =
    Multiplicity.getUnboundedInstance(1);

  /**
   * Private constructor.
   */
  private Multiplicity() { }

  /**
   * Returns a default instance.
   * @return the default instance
   */
  public static Multiplicity getDefaultInstance() { return ONE; }

  /**
   * Returns a bounded multiplicity.
   * @param lower the lower bound
   * @param upper the upper bound
   * @return the bounded instance.
   */
  public static Multiplicity getBoundedInstance(int lower, int upper) {
    Multiplicity instance = new Multiplicity();
    instance.setBounds(lower, upper);
    return instance;
  }

  /**
   * Returns an instance without an upper bound.
   * @param lower the lower bound
   * @return an instance with the specified lower bound and no upper limit
   */
  public static Multiplicity getUnboundedInstance(int lower) {
    Multiplicity instance = new Multiplicity();
    instance.setUnlimitedBounds(lower);
    return instance;
  }

  /**
   * Creates an instance for the specified string.
   * @param str the input string
   * @return the Multiplicity
   * @throws ParseException if str is not in the allowed format
   */
  public static Multiplicity getInstanceFromString(String str)
    throws ParseException {
    Pattern pattern = Pattern.compile("1|\\*|\\d+\\.\\.(\\d+|\\*)");
    if ("1".equals(str)) return Multiplicity.ONE;
    if ("*".equals(str)) return Multiplicity.N;
    if (pattern.matcher(str).matches()) {
      String[] comps = str.split("\\.\\.");
      int comp1 = Integer.valueOf(comps[0]);
      if ("*".equals(comps[1])) {
        return Multiplicity.getUnboundedInstance(comp1);
      }
      return Multiplicity.getBoundedInstance(comp1, Integer.valueOf(comps[1]));
    }
    throw new ParseException("could not parse '" +  str + "'", 0);
  }

  /**
   * Return the lower bound.
   * @return the lower bound
   */
  public int getLowerBound() { return lowerBound; }

  /**
   * Return the upper bound.
   * @return the upper bound
   */
  public int getUpperBound() { return upperBound; }

  /**
   * Sets the range in one step.
   * @param lower the lower bound
   * @param upper the upper bound
   */
  private void setBounds(int lower, int upper) {
    lowerBound = lower;
    upperBound = upper;
    noUpperLimit = false;
  }

  /**
   * Sets the range with no upper bound.
   * @param lower the lower bound
   */
  private void setUnlimitedBounds(int lower) {
    lowerBound = lower;
    noUpperLimit = true;
  }

  /**
   * Determines whether this multiplicity has no upper limit.
   * @return true if no upper limit, false otherwise
   */
  public boolean noUpperLimit() { return noUpperLimit; }

  /**
   * Determines whether the represented range is valid.
   * @return true if valid, false otherwise
   */
  public boolean isValid() {
    return lowerBound >= 0 && (lowerBound <= upperBound || noUpperLimit);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    if (noUpperLimit) return lowerBound == 0 ? "*" : lowerBound + "..*";
    if (lowerBound == upperBound) return String.valueOf(lowerBound);
    return lowerBound + ".." + upperBound;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj instanceof Multiplicity) {
      Multiplicity other = (Multiplicity) obj;
      return lowerBound == other.lowerBound &&
        ((!noUpperLimit && !other.noUpperLimit &&
         upperBound == other.upperBound) ||
        (noUpperLimit && other.noUpperLimit));
    }
    return false;
  }
}
