package net.bgx.bgxnetwork.persistence.metadata;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * User: O.Gerasimenko
 * Date: 02.03.2007
 * Time: 12:38:56
 * To change this template use File | Settings | File Templates.
 */
@Entity
@DiscriminatorValue("0")
public class GroupObjectType extends GroupType{
}
