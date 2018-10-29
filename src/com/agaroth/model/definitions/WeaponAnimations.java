package com.agaroth.model.definitions;

import com.agaroth.model.CharacterAnimations;
import com.agaroth.model.Item;
import com.agaroth.model.container.impl.Equipment;
import com.agaroth.world.entity.impl.player.Player;

public final class WeaponAnimations
{
  public static void assign(Player player, Item item)
  {
    player.getCharacterAnimations().reset();
    player.setCharacterAnimations(getUpdateAnimation(player));
  }
	public static void update(Player player) {
		//player.getCharacterAnimations().reset();
		player.setCharacterAnimations(getUpdateAnimation(player));
	}
	public static CharacterAnimations getUpdateAnimation(Player player) {
	Item item = player.getEquipment().getItems()[Equipment.WEAPON_SLOT];
	String weaponName = item.getDefinition().getName().toLowerCase();
    int playerStandIndex = 808;
    int playerWalkIndex = 819;
    int playerRunIndex = 824;
    if ((weaponName.contains("halberd")) || (weaponName.contains("guthan")))
    {
      playerStandIndex = 809;
      playerWalkIndex = 1146;
      playerRunIndex = 1210;
    }
    else if (weaponName.startsWith("basket"))
    {
      playerWalkIndex = 1836;
      playerRunIndex = 1836;
    }
    else if (weaponName.contains("dharok"))
    {
      playerStandIndex = 2065;
      playerWalkIndex = 1663;
      playerRunIndex = 1664;
    }
    else if (weaponName.contains("balmung"))
    {
      playerStandIndex = 2065;
      playerWalkIndex = 1663;
      playerRunIndex = 1664;
    }
    else if (weaponName.contains("sled"))
    {
      playerStandIndex = 1461;
      playerWalkIndex = 1468;
      playerRunIndex = 1467;
    }
    else if (weaponName.contains("ahrim"))
    {
      playerStandIndex = 809;
      playerWalkIndex = 1146;
      playerRunIndex = 1210;
    }
    else if (weaponName.contains("verac"))
    {
      playerStandIndex = 808;
      playerWalkIndex = 819;
      playerRunIndex = 824;
    }
    else if ((weaponName.contains("longsword")) || (weaponName.contains("scimitar")))
    {
      playerStandIndex = 11973;
      playerRunIndex = 15070;
      playerWalkIndex = 15073;
    }
    else if ((weaponName.contains("silverlight")) || 
      (weaponName.contains("korasi's")) || (weaponName.contains("katana")))
    {
      playerStandIndex = 11973;
      playerRunIndex = 12023;
      playerWalkIndex = 12024;
    }
    else if ((weaponName.contains("wand")) || (weaponName.contains("staff")) || 
      (weaponName.contains("staff")) || (weaponName.contains("spear")) || (item.getId() == 21005) || (item.getId() == 21010))
    {
      playerStandIndex = 8980;
      playerRunIndex = 1210;
      playerWalkIndex = 1146;
    }
    else if (weaponName.contains("karil"))
    {
      playerStandIndex = 2074;
      playerWalkIndex = 2076;
      playerRunIndex = 2077;
    }
    else if (weaponName.contains("blowpipe"))
    {
      playerStandIndex = 2074;
      playerWalkIndex = 2076;
      playerRunIndex = 2077;
    }
    else if ((weaponName.contains("2h sword")) || (weaponName.contains("godsword")) || 
      (weaponName.contains("saradomin sw")))
    {
      playerStandIndex = 7047;
      playerWalkIndex = 7046;
      playerRunIndex = 7039;
    }
    else if (weaponName.contains("bow"))
    {
      playerStandIndex = 808;
      playerWalkIndex = 819;
      playerRunIndex = 824;
    }
  /*  else if (weaponName.toLowerCase().equals("drygore rapier")) 
    {
      playerStandIndex = 11973;
      playerWalkIndex = 11975;
      playerRunIndex = 1661;
    } */
    else if (weaponName.toLowerCase().contains("rapier"))
    {
    	playerStandIndex = 11973;
        playerRunIndex = 15070;
        playerWalkIndex = 15073;
    }
    switch (item.getId())
    {
    case 14018: 
        playerStandIndex = 2052;
        playerWalkIndex = 13218;
        playerRunIndex = 13220;
        break;
    case 18353: 
      playerStandIndex = 13217;
      playerWalkIndex = 13218;
      playerRunIndex = 13220;
      break;
    case 16184: 
      playerStandIndex = 13217;
      playerWalkIndex = 13218;
      playerRunIndex = 13220;
      break;
    case 16425: 
      playerStandIndex = 13217;
      playerWalkIndex = 13218;
      playerRunIndex = 13220;
      break;
    case 4151: 
    case 13444: 
    case 15441: 
    case 15442: 
    case 15443: 
    case 15444: 
      playerStandIndex = 11973;
      playerWalkIndex = 11975;
      playerRunIndex = 1661;
      break;
    case 15039: 
      playerStandIndex = 12000;
      playerWalkIndex = 1663;
      playerRunIndex = 1664;
      break;
    case 10887: 
      playerStandIndex = 5869;
      playerWalkIndex = 5867;
      playerRunIndex = 5868;
      break;
    case 6528: 
    case 20084: 
      playerStandIndex = 2065;
      playerWalkIndex = 2064;
      playerRunIndex = 1664;
      break;
    case 4153: 
      playerStandIndex = 1662;
      playerWalkIndex = 1663;
      playerRunIndex = 1664;
      break;
    case 15241: 
      playerStandIndex = 12155;
      playerWalkIndex = 12154;
      playerRunIndex = 12154;
      break;
    case 11694: 
    case 11696: 
    case 11698: 
    case 11700: 
    case 11730: 
    case 20000: 
    case 20001: 
    case 20002: 
    case 20003: 
      break;
    case 1305: 
      playerStandIndex = 809;
    }
    return new CharacterAnimations(playerStandIndex, playerWalkIndex, playerRunIndex);
  }
  
  public static int getAttackAnimation(Player c)
  {
    int weaponId = c.getEquipment().getItems()[3].getId();
    String weaponName = ItemDefinition.forId(weaponId).getName().toLowerCase();
    String prop = c.getFightType().toString().toLowerCase();
    if (weaponId == 18349)
    {
    	if (prop.contains("stab")) {
            return 14816; //2394
          }
        if (prop.contains("lunge")) {
              return 14816;
          }
        if (prop.contains("slash")) {
              return 14816;
          }
        if (prop.contains("block")) {
              return 14816;
          }
    }
    
    
    if (weaponId == 21363) 
    {
      if (prop.contains("stab")) {
        return 14816; //2394
      }
      if (prop.contains("lunge")) {
          return 14816;
        }
      if (prop.contains("slash")) {
          return 14816;
        }
      if (prop.contains("block")) {
          return 14816;
        }
    }
    if (weaponId == 12926) {
    	return 13180; //2075 or 13180
    }
    if (weaponId == 18373) {
      return 1074;
    }
    if ((weaponId == 10033) || (weaponId == 10034)) {
      return 2779;
    }
    if (prop.contains("dart"))
    {
      if (prop.contains("long")) {
        return 6600;
      }
      return 582;
    }
    if ((weaponName.contains("javelin")) || (weaponName.contains("thrownaxe"))) {
      return 806;
    }
    if (weaponName.contains("halberd")) {
      return 440;
    }
    if (weaponName.startsWith("dragon dagger"))
    {
      if (prop.contains("slash")) {
        return 377;
      }
      return 376;
    }
    if (weaponName.endsWith("dagger"))
    {
      if (prop.contains("slash")) {
        return 13048;
      }
      return 13049;
    }
    if ((weaponName.equals("staff of light")) || (weaponId == 21005) || (weaponId == 21010))
    {
      if (prop.contains("stab")) {
        return 13044;
      }
      if (prop.contains("lunge")) {
        return 13047;
      }
      if (prop.contains("slash")) {
        return 13048;
      }
      if (prop.contains("block")) {
        return 13049;
      }
    }
    else if ((weaponName.startsWith("staff")) || (weaponName.endsWith("staff")))
    {
      return 401;
    }
    if ((weaponName.endsWith("warhammer")) || (weaponName.endsWith("battleaxe"))) {
      return 401;
    }
    if ((weaponName.contains("2h sword")) || (weaponName.contains("godsword")) || 
      (weaponName.contains("saradomin sword"))) {
      return 11979;
    }
    if (weaponName.contains("brackish"))
    {
      if ((prop.contains("lunge")) || (prop.contains("slash"))) {
        return 12029;
      }
      return 12028;
    }
    if ((weaponName.contains("scimitar")) || (weaponName.contains("longsword")) || 
      (weaponName.contains("korasi's")) || (weaponName.contains("katana")))
    {
      if (prop.contains("lunge")) {
        return 15072;
      }
      return 15071;
    }
    if (weaponName.contains("spear"))
    {
      if (prop.contains("lunge")) {
        return 13045;
      }
      if (prop.contains("slash")) {
        return 13047;
      }
      return 13044;
    }
    if (weaponName.contains("rapier"))
    {
      if (prop.contains("slash")) {
        return 14816;
    }
    }
    if (weaponName.contains("claws")) {
      return 393;
    }
    if ((weaponName.contains("maul")) && (!weaponName.contains("granite"))) {
      return 13055;
    }
    if (weaponName.contains("dharok"))
    {
      if (prop.contains("block")) {
        return 2067;
      }
      return 2066;
    }
    if (weaponName.contains("sword")) {
      return prop.contains("slash") ? 12311 : 12310;
    }
    if (weaponName.contains("karil")) {
      return 2075;
    }
    if ((weaponName.contains("'bow")) || (weaponName.contains("crossbow"))) {
      return 4230;
    }
    if ((weaponName.contains("bow")) && (!weaponName.contains("'bow"))) {
      return 426;
    }
    if (weaponName.contains("pickaxe"))
    {
      if (prop.contains("smash")) {
        return 401;
      }
      return 400;
    }
    if (weaponName.contains("mace"))
    {
      if (prop.contains("spike")) {
        return 13036;
      }
      return 13035;
    }
    switch (weaponId)
    {
    case 20000: 
    case 20001: 
    case 20002: 
    case 20003: 
      return 7041;
    case 6522: 
      return 2614;
    case 4153: 
      return 1665;
    case 13879: 
    case 13883: 
      return 806;
    case 16184: 
      return 2661;
    case 16425: 
      return 2661;
    case 15241: 
      return 12153;
    case 4747: 
      return 2068;
    case 4710: 
      return 406;
    case 18353: 
      return 13055;
    case 18349: 
      return 386;
    case 19146: 
      return 386;
    case 4755: 
      return 2062;
    case 4734: 
      return 2075;
    case 10887: 
      return 5865;
    case 4151: 
    case 13444: 
    case 15441: 
    case 15442: 
    case 15443: 
    case 15444: 
      if (prop.contains("flick")) {
        return 11968;
      }
      if (prop.contains("lash")) {
        return 11969;
      }
      if (prop.contains("deflect")) {
        return 11970;
      }
    case 6528: 
    case 20084: 
      return 2661;
    }
    return c.getFightType().getAnimation();
}
  
  public static int getBlockAnimation(Player c)
  {
    int weaponId = c.getEquipment().getItems()[3].getId();
    String shield = ItemDefinition.forId(c.getEquipment().getItems()[5].getId()).getName().toLowerCase();
    String weapon = ItemDefinition.forId(weaponId).getName().toLowerCase();
    if (shield.contains("defender")) {
      return 4177;
    }
    if (shield.contains("2h")) {
      return 7050;
    }
    if ((shield.contains("book")) && (weapon.contains("wand"))) {
      return 420;
    }
    if (shield.contains("shield")) {
      return 1156;
    }
    if ((weapon.contains("scimitar")) || (weapon.contains("longsword")) || (weapon.contains("korasi")) || (weapon.contains("rapier"))) {
      return 15074;
    }
    switch (weaponId)
    {
    case 4755: 
      return 2063;
    case 15241: 
      return 12156;
    case 13899: 
      return 13042;
    case 18355: 
    case 20314: 
      return 13046;
    case 14484: 
      return 397;
    case 11716: 
      return 12008;
    case 4153: 
      return 1666;
    case 4151: 
    case 13444: 
    case 15441: 
    case 15442: 
    case 15443: 
    case 15444: 
      return 11974;
    case 14004: 
    case 14005: 
    case 14006: 
    case 14007: 
    case 15486: 
    case 15502: 
    case 21005: 
    case 21010: 
    case 22207: 
    case 22209: 
    case 22211: 
    case 22213: 
      return 12806;
    case 18349: 
      return 12030;
    case 18353: 
      return 13054;
    case 18351: 
      return 13042;
    case 11694: 
    case 11696: 
    case 11698: 
    case 11700: 
    case 11730: 
    case 20000: 
    case 20001: 
    case 20002: 
    case 20003: 
      return 7050;
    case -1: 
      return 424;
    }
    return 424;
  }
}
