package net.bgx.bgxnetwork.persistence.view;

import net.bgx.bgxnetwork.persistence.metadata.GroupType;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * User: O.Gerasimenko
 * Date: 02.03.2007
 * Time: 13:52:29
 * To change this template use File | Settings | File Templates.
 */
@Entity
@DiscriminatorValue("2")
public class GroupLabelType extends GroupType {
}
