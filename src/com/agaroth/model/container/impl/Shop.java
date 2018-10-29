package com.agaroth.model.container.impl;

import java.util.HashMap;
import java.util.Map;

import com.agaroth.engine.task.TaskManager;
import com.agaroth.engine.task.impl.ShopRestockTask;
import com.agaroth.model.GameMode;
import com.agaroth.model.Item;
import com.agaroth.model.PlayerRights;
import com.agaroth.model.Skill;
import com.agaroth.model.container.ItemContainer;
import com.agaroth.model.container.StackType;
import com.agaroth.model.definitions.ItemDefinition;
import com.agaroth.model.input.impl.EnterAmountToBuyFromShop;
import com.agaroth.model.input.impl.EnterAmountToSellToShop;
import com.agaroth.util.JsonLoader;
import com.agaroth.util.Misc;
import com.agaroth.world.World;
import com.agaroth.world.content.minigames.impl.RecipeForDisaster;
import com.agaroth.world.entity.impl.player.Player;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Shop extends ItemContainer {
	public Shop(Player player, int id, String name, Item currency, Item[] stockItems) {
		super(player);
		if (stockItems.length > 42)
			throw new ArrayIndexOutOfBoundsException("Stock cannot have more than 40 items; check shop[" + id + "]: stockLength: " + stockItems.length);
		this.id = id;
		this.name = name.length() > 0 ? name : "General Store";
		this.currency = currency;
		this.originalStock = new Item[stockItems.length];
		for(int i = 0; i < stockItems.length; i++) {
			Item item = new Item(stockItems[i].getId(), stockItems[i].getAmount());
			add(item, false);
			this.originalStock[i] = item;
		}
	}

	private final int id;
	private String name;
	private Item currency;
	private Item[] originalStock;

	public Item[] getOriginalStock() {
		return this.originalStock;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}

	public Shop setName(String name) {
		this.name = name;
		return this;
	}

	public Item getCurrency() {
		return currency;
	}

	public Shop setCurrency(Item currency) {
		this.currency = currency;
		return this;
	}

	private boolean restockingItems;

	public boolean isRestockingItems() {
		return restockingItems;
	}

	public void setRestockingItems(boolean restockingItems) {
		this.restockingItems = restockingItems;
	}

	public Shop open(Player player) {
		setPlayer(player);
		getPlayer().getPacketSender().sendInterfaceRemoval().sendClientRightClickRemoval();
		getPlayer().setShop(ShopManager.getShops().get(id)).setInterfaceId(INTERFACE_ID).setShopping(true);
		refreshItems();
		return this;
	}

	public void publicRefresh() {
		Shop publicShop = ShopManager.getShops().get(id);
		if (publicShop == null)
			return;
		publicShop.setItems(getItems());
		for (Player player : World.getPlayers()) {
			if(player == null)
				continue;
			if (player.getShop() != null && player.getShop().id == id && player.isShopping())
				player.getShop().setItems(publicShop.getItems());
		}
	}

	public void checkValue(Player player, int slot, boolean sellingItem) {
		this.setPlayer(player);
		Item shopItem = new Item(getItems()[slot].getId());
		if(!player.isShopping()) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		Item item = sellingItem ? player.getInventory().getItems()[slot] : getItems()[slot];
		if(item.getId() == 995)
			return;
		if(sellingItem) {
			if(!shopBuysItem(id, item)) {
				player.getPacketSender().sendMessage("You cannot sell this item to this store.");
				return;
			}
		}
		int finalValue = 0;
		String finalString = sellingItem ? ""+ItemDefinition.forId(item.getId()).getName()+": shop will buy for " : ""+ItemDefinition.forId(shopItem.getId()).getName()+" currently costs ";
		if(getCurrency().getId() != -1) {
			finalValue = ItemDefinition.forId(item.getId()).getValue();
			String s = currency.getDefinition().getName().toLowerCase().endsWith("s") ? currency.getDefinition().getName().toLowerCase() : currency.getDefinition().getName().toLowerCase() + "s";
			if(id == TOKKUL_EXCHANGE_STORE || id == ENERGY_FRAGMENT_STORE || id == AGILITY_TICKET_STORE || id == GRAVEYARD_STORE) {
				Object[] obj = ShopManager.getCustomShopData(id, item.getId());
				if(obj == null)
					return;
				finalValue = (int) obj[0];
				s = (String) obj[1];
			}
			if(sellingItem) {
				if(finalValue != 1) {
					finalValue = (int) (finalValue * 0.85);	
				}
			}
			finalString += ""+(int) finalValue+" "+s+""+shopPriceEx((int) finalValue)+".";
		} else {
			Object[] obj = ShopManager.getCustomShopData(id, item.getId());
			if(obj == null)
				return;
			finalValue = (int) obj[0];
			if(sellingItem) {
				if(finalValue != 1) {
					finalValue = (int) (finalValue * 0.85);	
				}
			}
			finalString += ""+finalValue+" " + (String) obj[1] + ".";
		}
		if(player!= null && finalValue > 0) {
			player.getPacketSender().sendMessage(finalString);
			return;
		}
	}

	public void sellItem(Player player, int slot, int amountToSell) {
		this.setPlayer(player);
		if(!player.isShopping() || player.isBanking()) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		if(id == GENERAL_STORE) {
			if(player.getRights() == PlayerRights.ADMINISTRATOR || player.getRights() == PlayerRights.OWNER) {
				player.getPacketSender().sendMessage("You cannot sell items.");
				return;
			}
		}
		if(!player.isShopping() || player.isBanking()) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		} 
		Item itemToSell = player.getInventory().getItems()[slot];
		if(!itemToSell.sellable()) {
			player.getPacketSender().sendMessage("This item cannot be sold.");
			return;
		}
		if(!shopBuysItem(id, itemToSell)) {
			player.getPacketSender().sendMessage("You cannot sell this item to this store.");
			return;
		}
		if(!player.getInventory().contains(itemToSell.getId()) || itemToSell.getId() == 995)
			return;
		if(this.full(itemToSell.getId()))
			return;
		if(player.getInventory().getAmount(itemToSell.getId()) < amountToSell)
			amountToSell = player.getInventory().getAmount(itemToSell.getId());
		if(amountToSell == 0)
			return;
		int itemId = itemToSell.getId();
		boolean customShop = this.getCurrency().getId() == -1;
		boolean inventorySpace = customShop ? true : false;
		if(!customShop) {
			if(!itemToSell.getDefinition().isStackable()) {
				if(!player.getInventory().contains(this.getCurrency().getId()))
					inventorySpace = true;
			}
			if(player.getInventory().getFreeSlots() <= 0 && player.getInventory().getAmount(this.getCurrency().getId()) > 0)
				inventorySpace = true;
			if(player.getInventory().getFreeSlots() > 0 || player.getInventory().getAmount(this.getCurrency().getId()) > 0)
				inventorySpace = true;
		}
		int itemValue = 0;
		if(getCurrency().getId() > 0) {
			itemValue = ItemDefinition.forId(itemToSell.getId()).getValue();
		} else {
			Object[] obj = ShopManager.getCustomShopData(id, itemToSell.getId());
			if(obj == null)
				return;
			itemValue = (int) obj[0];
		}
		if(itemValue <= 0)
			return;
		itemValue = (int) (itemValue * 0.85);
		if(itemValue <= 0) {
			itemValue = 1;
		}
		for (int i = amountToSell; i > 0; i--) {
			itemToSell = new Item(itemId);
			if(this.full(itemToSell.getId()) || !player.getInventory().contains(itemToSell.getId()) || !player.isShopping())
				break;
			if(!itemToSell.getDefinition().isStackable()) {
				if(inventorySpace) {
					super.switchItem(player.getInventory(), this, itemToSell.getId(), -1);
					if(!customShop) {
						player.getInventory().add(new Item(getCurrency().getId(), itemValue), false);
					} else {
					}
				} else {
					player.getPacketSender().sendMessage("Please free some inventory space before doing that.");
					break;
				}
			} else {
				if(inventorySpace) {
					super.switchItem(player.getInventory(), this, itemToSell.getId(), amountToSell);
					if(!customShop) {
						player.getInventory().add(new Item(getCurrency().getId(), itemValue * amountToSell), false);
					} else {
					}
					break;
				} else {
					player.getPacketSender().sendMessage("Please free some inventory space before doing that.");
					break;
				}
			}
			amountToSell--;
		}
		if(customShop) {
			player.getPointsHandler().refreshPanel();
		}
		player.getInventory().refreshItems();
		fireRestockTask();
		refreshItems();
		publicRefresh();
	}

	@Override
	public Shop switchItem(ItemContainer to, Item item, int slot, boolean sort, boolean refresh) {
		final Player player = getPlayer();
		if(player == null)
			return this;
		if(!player.isShopping() || player.isBanking()) {
			player.getPacketSender().sendInterfaceRemoval();
			return this;
		}
		if(player.getGameMode() != GameMode.NORMAL && this.id == SUMMOMING1) {
			if (item.getId() == 3095 || item.getId() == 9976) {
				player.getPacketSender().sendMessage("Ironmen are not allowed to buy this item as it can be collected elsewhere.");
				return this;
			}
		}
		if(player.getGameMode() != GameMode.NORMAL && this.id == SUMMOMING2) {
			if (item.getId() == 1963 || item.getId() == 2460 || item.getId() == 1115 || item.getId() == 1119 || item.getId() == 10818 || item.getId() == 1635 || item.getId() == 2132 || item.getId() == 9978) {
				player.getPacketSender().sendMessage("Ironmen are not allowed to buy this item as it can be collected elsewhere.");
				return this;
			}
		}
		if(player.getGameMode() != GameMode.NORMAL && this.id == HERBLORE_STORE) {
			if (item.getId() == 199 || item.getId() == 4621 || item.getId() == 5972) {
				player.getPacketSender().sendMessage("Ironmen are not allowed to buy this item as it can be collected elsewhere.");
				return this;
			}
		}
		if(this.id == GENERAL_STORE || this.id == ARMOUR_STORE
				 || this.id == CRAFTING_STORE || this.id == RUNECRAFTING_STORE || this.id == POTIONS_STORE
				 || this.id == FARMING_STORE || this.id == COSTUME_STORE || this.id == PURE_PVP
				 || this.id == FISHING_STORE || this.id == PRAYER_STORE || this.id == COOKING_STORE
				 || this.id == FIREMAKING_STORE || this.id == WOODCUT_STORE || this.id == MINING_STORE
				 || this.id == MERCHANT_STORE || this.id == JEWELRIES_STORE || this.id == CONSUMABLES
				 || this.id == MELEE_WEAPONS || this.id == MELEE_EQUIPMENT || this.id == RANGED_EQUIPMENT
				 || this.id == AMMUNITION || this.id == MAGIC_EQUIPMENT || this.id == RUNES) {
			if(player.getGameMode() == GameMode.IRONMAN) {
				player.getPacketSender().sendMessage("Ironman-players are not allowed to buy items from the this store.");
				return this;
			}
			if(player.getGameMode() == GameMode.HARDCORE_IRONMAN) {
				player.getPacketSender().sendMessage("Hardcore-ironman-players are not allowed to buy items from the this store.");
				return this;
			}
			
		}
		if (!shopSellsItem(item))
			return this;
		if(getItems()[slot].getAmount() <= 1 && id != GENERAL_STORE) {
			player.getPacketSender().sendMessage("The shop has run out of stock for this item.");
			return this;
		}
		if (item.getAmount() > getItems()[slot].getAmount())
			item.setAmount(getItems()[slot].getAmount());
		int amountBuying = item.getAmount();
		if(amountBuying == 0)
			return this;
		if(amountBuying > 5000) {
			player.getPacketSender().sendMessage("You can only buy 5000 "+ItemDefinition.forId(item.getId()).getName()+"s at a time.");
			return this;
		}
		boolean customShop = getCurrency().getId() == -1;
		boolean usePouch = false;
		int playerCurrencyAmount = 0;
		int value = ItemDefinition.forId(item.getId()).getValue();
		String currencyName = "";
		if(getCurrency().getId() != -1) {
			playerCurrencyAmount = player.getInventory().getAmount(currency.getId());
			currencyName = ItemDefinition.forId(currency.getId()).getName().toLowerCase();
			if(currency.getId() == 995) {
				if(player.getMoneyInPouch() >= value) {
					playerCurrencyAmount = player.getMoneyInPouchAsInt();
					if(!(player.getInventory().getFreeSlots() == 0 && player.getInventory().getAmount(currency.getId()) == value)) {
						usePouch = true;
					}
				}
			} else {
				if(id == TOKKUL_EXCHANGE_STORE || id == ENERGY_FRAGMENT_STORE || id == AGILITY_TICKET_STORE || id == GRAVEYARD_STORE) {
					value = (int) ShopManager.getCustomShopData(id, item.getId())[0];
				}
			}
		} else {
			Object[] obj = ShopManager.getCustomShopData(id, item.getId());
			if(obj == null)
				return this;
			value = (int) obj[0];
			currencyName = (String) obj[1];
			if ((this.id == 48) || (this.id == 49) || (this.id == 50) || (this.id == 51) || (this.id == 52)) {
			        playerCurrencyAmount = player.getPointsHandler().getDonatorPoints();
			}
			else if(id == PKING_REWARDS_STORE) {
				playerCurrencyAmount = player.getPointsHandler().getPkPoints();
			} else if(id == VOTING_REWARDS_STORE) {
				playerCurrencyAmount = player.getPointsHandler().getVotingPoints();
			} else if(id == 55) {
				playerCurrencyAmount = player.getPointsHandler().getIronManPoints();
			} else if(id == DUNGEONEERING_STORE) {
				playerCurrencyAmount = player.getPointsHandler().getDungeoneeringTokens();
			} else if(id == PRESTIGE_STORE) {
				playerCurrencyAmount = player.getPointsHandler().getPrestigePoints();
			} else if(id == LOYALTY_STORE) {
				playerCurrencyAmount = player.getPointsHandler().getLoyaltyPoints();
			} else if(id == SLAYER_STORE) {
				playerCurrencyAmount = player.getPointsHandler().getSlayerPoints();
			}
		}
		if(value <= 0) {
			return this;
		}
		if(!hasInventorySpace(player, item, getCurrency().getId(), value)) {
			player.getPacketSender().sendMessage("You do not have any free inventory slots.");
			return this;
		}
		if (playerCurrencyAmount <= 0 || playerCurrencyAmount < value) {
			player.getPacketSender().sendMessage("You do not have enough " + ((currencyName.endsWith("s") ? (currencyName) : (currencyName + "s"))) + " to purchase this item.");
			return this;
		}
		if(id == SKILLCAPE_STORE_1 || id == SKILLCAPE_STORE_2 || id == SKILLCAPE_STORE_3) {
			for(int i = 0; i < item.getDefinition().getRequirement().length; i++) {
				int req = item.getDefinition().getRequirement()[i];
				if((i == 3 || i == 5) && req == 99)
					req *= 10;
				if(req > player.getSkillManager().getMaxLevel(i)) {
					player.getPacketSender().sendMessage("You need to have at least level 99 in "+Misc.formatText(Skill.forId(i).toString().toLowerCase())+" to buy this item.");
					return this;
				}
			}
		} else if(id == GAMBLING_STORE) {
			if(item.getId() == 15084 || item.getId() == 299) {
				if(player.getAmountDonated() <= 6) {
					player.getPacketSender().sendMessage("You need to be a donator to use these items.");
					return this;
				}
			}
		}

		for (int i = amountBuying; i > 0; i--) {
			if (!shopSellsItem(item)) {
				break;
			}
			if(getItems()[slot].getAmount() <= 1 && id != GENERAL_STORE) {
				player.getPacketSender().sendMessage("The shop has run out of stock for this item.");
				break;
			}
			if(!item.getDefinition().isStackable()) {
				if(playerCurrencyAmount >= value && hasInventorySpace(player, item, getCurrency().getId(), value)) {

					if(!customShop) {
						if(usePouch) {
							player.setMoneyInPouch((player.getMoneyInPouch() - value));
						} else {
							player.getInventory().delete(currency.getId(), value, false);
						}
					} else {
						if(id == PKING_REWARDS_STORE) {
							player.getPointsHandler().setPkPoints(-value, true);
						} else if ((this.id == 48) || (this.id == 49) || (this.id == 50) || (this.id == 51) || (this.id == 52)) {
					          player.getPointsHandler().setDonatorPoints(-value, true);
				        } else if(id == 55) {
				        	 player.getPointsHandler().setIronManPoints(-value, true);
						} else if(id == VOTING_REWARDS_STORE) {
							player.getPointsHandler().setVotingPoints(-value, true);
						} else if(id == LOYALTY_STORE) {
							player.getPointsHandler().setLoyaltyPoints(-value, true);
						} else if(id == DUNGEONEERING_STORE) {
							player.getPointsHandler().setDungeoneeringTokens(-value, true);
						} else if(id == PRESTIGE_STORE) {
							player.getPointsHandler().setPrestigePoints(-value, true);
						} else if(id == SLAYER_STORE) {
							player.getPointsHandler().setSlayerPoints(-value, true);
						}
					}

					super.switchItem(to, new Item(item.getId(), 1), slot, false, false);

					playerCurrencyAmount -= value;
				} else {
					break;
				}
			} else {
				if(playerCurrencyAmount >= value && hasInventorySpace(player, item, getCurrency().getId(), value)) {
					
					int canBeBought = playerCurrencyAmount / (value);
					if(canBeBought >= amountBuying) {
						canBeBought = amountBuying;
					}
					if(canBeBought == 0)
						break;
					
					if(!customShop) {
						if(usePouch) {
							player.setMoneyInPouch((player.getMoneyInPouch() - (value * canBeBought)));
						} else {
							player.getInventory().delete(currency.getId(), value * canBeBought, false);
						}
					} else {
						if(id == PKING_REWARDS_STORE) {
							player.getPointsHandler().setPkPoints(-value * canBeBought, true);
						} else if ((this.id == 48) || (this.id == 49) || (this.id == 50) || (this.id == 51) || (this.id == 52)) {
					          player.getPointsHandler().setDonatorPoints(-value * canBeBought, true);
				        } else if(id == 55) {
				        	player.getPointsHandler().setIronManPoints(-value * canBeBought, true);
						} else if(id == VOTING_REWARDS_STORE) {
							player.getPointsHandler().setVotingPoints(-value * canBeBought, true);
						} else if(id == DUNGEONEERING_STORE) {
							player.getPointsHandler().setDungeoneeringTokens(-value * canBeBought, true);
						} else if(id == PRESTIGE_STORE) {
							player.getPointsHandler().setPrestigePoints(-value * canBeBought, true);
						} else if(id == LOYALTY_STORE) {
							player.getPointsHandler().setLoyaltyPoints(-value * canBeBought, true);
						} else if(id == SLAYER_STORE) {
							player.getPointsHandler().setSlayerPoints(-value * canBeBought, true);
						}
					}
					super.switchItem(to, new Item(item.getId(), canBeBought), slot, false, false);
					playerCurrencyAmount -= value;
					break;
				} else {
					break;
				}
			}
			amountBuying--;
		}
		if(!customShop) {
			if(usePouch) {
				player.getPacketSender().sendString(8135, ""+player.getMoneyInPouch()); //Update the money pouch
			}
		} else {
			player.getPointsHandler().refreshPanel();
		}
		player.getInventory().refreshItems();
		fireRestockTask();
		refreshItems();
		publicRefresh();
		return this;
	}

	public static boolean hasInventorySpace(Player player, Item item, int currency, int pricePerItem) {
		if(player.getInventory().getFreeSlots() >= 1) {
			return true;
		}
		if(item.getDefinition().isStackable()) {
			if(player.getInventory().contains(item.getId())) {
				return true;
			}
		}
		if(currency != -1) {
			if(player.getInventory().getFreeSlots() == 0 && player.getInventory().getAmount(currency) == pricePerItem) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Shop add(Item item, boolean refresh) {
		super.add(item, false);
		if(id != RECIPE_FOR_DISASTER_STORE)
			publicRefresh();
		return this;
	}

	@Override
	public int capacity() {
		return 42;
	}

	@Override
	public StackType stackType() {
		return StackType.STACKS;
	}

	@Override
	public Shop refreshItems() {
		if(id == RECIPE_FOR_DISASTER_STORE) {
			RecipeForDisaster.openRFDShop(getPlayer());
			return this;
		}
		for (Player player : World.getPlayers()) {
			if (player == null || !player.isShopping() || player.getShop() == null || player.getShop().id != id)
				continue;
			player.getPacketSender().sendItemContainer(player.getInventory(), INVENTORY_INTERFACE_ID);
			player.getPacketSender().sendItemContainer(ShopManager.getShops().get(id), ITEM_CHILD_ID);
			player.getPacketSender().sendString(NAME_INTERFACE_CHILD_ID, name);
			if(player.getInputHandling() == null || !(player.getInputHandling() instanceof EnterAmountToSellToShop || player.getInputHandling() instanceof EnterAmountToBuyFromShop))
				player.getPacketSender().sendInterfaceSet(INTERFACE_ID, INVENTORY_INTERFACE_ID - 1);
		}
		return this;
	}

	@Override
	public Shop full() {
		getPlayer().getPacketSender().sendMessage("The shop is currently full. Please come back later.");
		return this;
	}

	public String shopPriceEx(int shopPrice) {
		String ShopAdd = "";
		if (shopPrice >= 1000 && shopPrice < 1000000) {
			ShopAdd = " (" + (shopPrice / 1000) + "K)";
		} else if (shopPrice >= 1000000) {
			ShopAdd = " (" + (shopPrice / 1000000) + " million)";
		}
		return ShopAdd;
	}

	private boolean shopSellsItem(Item item) {
		return contains(item.getId());
	}

	public void fireRestockTask() {
		if(isRestockingItems() || fullyRestocked())
			return;
		setRestockingItems(true);
		TaskManager.submit(new ShopRestockTask(this));
	}

	public boolean fullyRestocked() {
		if(id == GENERAL_STORE) {
			return getValidItems().size() == 0;
		} else if(id == RECIPE_FOR_DISASTER_STORE) {
			return true;
		}
		if(getOriginalStock() != null) {
			for(int shopItemIndex = 0; shopItemIndex < getOriginalStock().length; shopItemIndex++) {
				if(getItems()[shopItemIndex].getAmount() != getOriginalStock()[shopItemIndex].getAmount())
					return false;
			}
		}
		return true;
	}

	public static boolean shopBuysItem(int shopId, Item item) {
		if(shopId == GENERAL_STORE)
			return true;
		if(shopId == 48 || shopId == 49 || shopId == 55 || shopId == 50 || shopId == 51 || shopId == 52||shopId == DUNGEONEERING_STORE || shopId == LOYALTY_STORE || shopId == PKING_REWARDS_STORE || shopId == VOTING_REWARDS_STORE || shopId == RECIPE_FOR_DISASTER_STORE || shopId == ENERGY_FRAGMENT_STORE || shopId == AGILITY_TICKET_STORE || shopId == GRAVEYARD_STORE || shopId == TOKKUL_EXCHANGE_STORE || shopId == PRESTIGE_STORE || shopId == SLAYER_STORE)
			return false;
		Shop shop = ShopManager.getShops().get(shopId);
		if(shop != null && shop.getOriginalStock() != null) {
			for(Item it : shop.getOriginalStock()) {
				if(it != null && it.getId() == item.getId())
					return true;
			}
		}
		return false;
	}

	public static class ShopManager {

		private static Map<Integer, Shop> shops = new HashMap<Integer, Shop>();

		public static Map<Integer, Shop> getShops() {
			return shops;
		}

		public static JsonLoader parseShops() {
			return new JsonLoader() {
				@Override
				public void load(JsonObject reader, Gson builder) {
					int id = reader.get("id").getAsInt();
					String name =  reader.get("name").getAsString();
					Item[] items = builder.fromJson(reader.get("items").getAsJsonArray(), Item[].class);
					Item currency = new Item(reader.get("currency").getAsInt());
					shops.put(id, new Shop(null, id, name, currency, items));
				}

				@Override
				public String filePath() {
					return "./data/def/json/world_shops.json";
				}
			};
		}

		public static Object[] getCustomShopData(int shop, int item) {
			if (shop == VOTING_REWARDS_STORE) {
		        switch (item) {
		        case 6500: 
		          return new Object[] {Integer.valueOf(85), "Voting points" };
		        case 19336: 
		          return new Object[] {Integer.valueOf(120), "Voting points" };
		        case 19337: 
		        case 19338: 
		        case 19339: 
		          return new Object[] {Integer.valueOf(100), "Voting points" };
		        case 9813: 
		        case 19340: 
		          return new Object[] {Integer.valueOf(75), "Voting points" };
		        case 20084: 
		          return new Object[] {Integer.valueOf(30), "Voting points" };
		        case 11846: 
		        case 11850: 
		        case 11852: 
		        case 11854: 
		        case 11856: 
		        case 15018: 
		        case 15019: 
		        case 15020: 
		        case 15220: 
		        case 15501: 
		        case 19111: 
		          return new Object[] {Integer.valueOf(35), "Voting points" };
		        case 6585: 
		          return new Object[] {Integer.valueOf(40), "Voting points" };
		        case 19335: 
		          return new Object[] {Integer.valueOf(60), "Voting points" };
		        case 6570: 
		          return new Object[] {Integer.valueOf(10), "Voting points" };
		        case 6199: 
		          return new Object[] {Integer.valueOf(10), "Voting points" };
		        case 13262: 
		        case 14004: 
		        case 14005: 
		        case 14006: 
		        case 14007: 
		        case 15441: 
		        case 15442: 
		        case 15443: 
		        case 15444: 
		          return new Object[] {Integer.valueOf(15), "Voting points" };
		        case 15332: 
		          return new Object[] {Integer.valueOf(2), "Voting points" };
		        case 2577: 
		        case 14000: 
		        case 14001: 
		        case 14002: 
		        case 14003: 
		          return new Object[] {Integer.valueOf(20), "Voting points" };
		        case 11848: 
		          return new Object[] {Integer.valueOf(35), "Voting points" };
		        case 13663: 
		        case 20157: 
		          return new Object[] {Integer.valueOf(6), "Voting points" };
		       }
		     } else if (shop == 48) {
		          switch (item) {
		          case 11694: //Armadyl godsword
			            return new Object[] { Integer.valueOf(15), "Donator points @red@($15.0)" };
		          case 18349: //Chaotic rapier
			            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 20314: //Staff of gods
			            return new Object[] { Integer.valueOf(20), "Donator points @red@($20.0) " };
		          case 20203: //Drygore longsword
			            return new Object[] { Integer.valueOf(15), "Donator points @red@($15.0)" };
		          case 21363: //Drygore rapier
		          case 20202: //Drygore mace
			            return new Object[] { Integer.valueOf(15), "Donator points @red@($15.0)" };
		          case 20100: //Arma cbow
			            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0)" };
		          case 19780: //Korasi
			            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0)" };
		          case 14484: //Dragon claws
			            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0)" };
		          case 14018: //Ornate katana
		          case 15485: //Penance Master Trident
			            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0) -50%" };
		          case 13899: //vesta`s longsword
		          case 18351: //Chaotic longsword
		          case 18353: //Chaotic maul
		          case 18355: //chaotic crossbow
		          case 18357: //Chaotic staff
		            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 13905: //Vesta spear
		            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 11698: //Saradomin godswrd
		          case 13902: //Statius warhammer
		            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0) - 50%" };
		          case 11700: //Zamorak godsword
		          case 15241: //Handcannon
		          case 20084: //Golden maul
		            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 14017: //Brackish blade
		          case 11235: //Dark bow
		          case 11696: //Bandos godsword
		          case 11730: //Saradomin sword
		          case 11716: //zmorak spear
		            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 15486: //Staff of light
		          case 4151: //Abyssal whip
		            return new Object[] { Integer.valueOf(2), "Donator points @red@($2.0)" };
		         }
		       } else if (shop == 49) {
		          switch (item) {
		          case 14009: //Torva platebody
		          case 14010: //Torva platelegs
		          case 14012: //Pernix body
		          case 14013: //Pernix chaps
		          case 14015: //Virtus body
		          case 14016: //virtus bottoms
		            return new Object[] { Integer.valueOf(7), "Donator points @red@($7.0)" };
		          case 13887: //Vesta body
		          case 13893: //Vesta skirt
		          case 13884: //Statius platebody
		          case 13890: //Statius platelegs
		          case 13858: //Zuriel top
		          case 13861: //Zuriel bottom
		          case 13870: //Morrigan top
		          case 13873: //morrigan legs
		        	  return new Object[] { Integer.valueOf(7), "Donator points @red@($7.0)" };
		          case 11720: //Armadyl chest
		          case 11722: //Armadyl skirt
		          case 11724: //Bandos chest
		          case 11726: //Bandos tasset
		            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 19785: //E Void top
		          case 19786: //E Void bottom
		            return new Object[] { Integer.valueOf(3), "Donator points @red@($3.0) - 50%" };
		          case 14008: //Torva helm
		          case 14011: //Pernix cowl
		          case 14014: //Virtus mask
		            return new Object[] { Integer.valueOf(7), "Donator points @red@($7.0)" };
		          case 13896: //Statius helm
		          case 13864: //Zuriel hood
		          case 13876: //Morrigan coif
			            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 11718: //Armadyl helm
			            return new Object[] { Integer.valueOf(2), "Donator points @red@($2.0)" };
		          case 11728: //Bandos boots
			            return new Object[] { Integer.valueOf(2), "Donator points @red@($2.0)" };
		          case 10330: //3a
		          case 10332:
		          case 10338:
		          case 10340:
		          case 10346:
		          case 10348:
			            return new Object[] { Integer.valueOf(15), "Donator points @red@($15.0)" };
		          case 10334:
		          case 10352:
			            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 10350:
		          case 10342:
			            return new Object[] { Integer.valueOf(7), "Donator points @red@($7.0)" };
		          case 10336:
		          case 10344:
			            return new Object[] { Integer.valueOf(7), "Donator points @red@($7)" };
		          
		          }
		       } else if (shop == 50) {
		          switch (item) {
		          case 20153: //Occult necklace
		          case 20311: //Dragonbone boots
		          case 20310: //Dragonbone gloves
			            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 13740: //Divine
			            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0)" };
		          case 13744: //Spectral
			            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0) - 50%" };
		          case 21347: //Tetsu plate
		          case 21349: //Tetsu legs
		          case 21350: //deathlotus
		          case 21351: //deathlotus
		          case 21352: //deathlotus
			            return new Object[] { Integer.valueOf(15), "Donator points @red@($15.0)" };
		          case 20308: //Dragonbone plate
		          case 20312: //Dragonbone legs
		          case 20302: //Dragonbone plate
		          case 20304: //Dragonbone legs
			            return new Object[] { Integer.valueOf(15), "Donator points @red@($15.0)" };
		          case 13742: //Elysian
		            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0)" };
		         case 20158: //Upgraded malediction
		          case 20159: //Upgraded Odium
		          case 20204: //Tetsu helm'
		          case 20214: //ahrim book
		            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0)" };
		          case 13738: //Arcane
		          case 20112: //Malediction ward
		          case 20113: //Odium ward
		          case 20307: //Dragonbone helm
		          case 20301: //Dragonbone legs
		            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 18359: //Chaotic kite
		          case 18361: //Eagle-eye
		          case 18363: //Farseer
		            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 20000: //Steads
		          case 20001: //Ragefire
		            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 20002: //Glaiven
		          case 11283: //Dragonfire shield
		            return new Object[] { Integer.valueOf(3), "Donator points @red@($3.0) - 50%" };
		          case 20116: //Upgrade kit
		            return new Object[] { Integer.valueOf(2), "Donator points @red@($2.0)" };
		         }
		       } else if (shop == 51) {
		          switch (item) {
		          case 20271: //Briefcase
			            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0)" };
		          case 20313: //anon mask
			            return new Object[] { Integer.valueOf(20), "Donator points @red@($20.0)" };
		          case 1038: //Red phat
			            return new Object[] { Integer.valueOf(20), "Donator points @red@($20.0)" };
		          case 20139: //partyhat and specs
			            return new Object[] { Integer.valueOf(20), "Donator points @red@($20.0)" };
		          case 1044: //White phat
			            return new Object[] { Integer.valueOf(20), "Donator points @red@($20.0)" };
		          case 1048: //Yellow phat
			            return new Object[] { Integer.valueOf(20), "Donator points @red@($20.0) - 50%" };
		          case 1040: //Blue phat
		          case 1042: //Green phat
		          case 1046: //Purple phat
		            return new Object[] { Integer.valueOf(20), "Donator points @red@($20.0)" };
		          case 19308: //3rd age staff
		          case 19317: //3rd age top
		          case 19320: //3rd age bot
			            return new Object[] { Integer.valueOf(15), "Donator points @red@($15.0)" };
		          case 1419: //Scythe
			            return new Object[] { Integer.valueOf(15), "Donator points @red@($15.0)" };
		          case 1053: //Red mask
		          case 1057: //Green mask
		          case 19314: //3rd age wreath
		            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0)" };
		          case 1055: //Blue hween
			            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0) - 50%" };
		          case 1037: //Bunny ears
		            return new Object[] { Integer.valueOf(15), "Donator points @red@($15.0)" };
		          case 1050: //Santa
		            return new Object[] { Integer.valueOf(20), "Donator points @red@($20.0)" };
		          case 19311: //3rd age cloak
		            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0) - 50%" };
		          case 20142: //Musketeer hat
			        return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 20044: //Witchdoctor top
		          case 20045: //Witchdoctor legs
		          case 20046: //Witchdoctor mask
		            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 19893: //spirit cape
		            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 19272: //fox mask
			            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 19275: //white uni mask
		          case 19278: //black uni mask
		          case 19293: //frost mask
		          case 19296: //bronze mask
		          case 19299: //iron mask
		          case 19302: //steel mask
		          case 19305: //mithril mask
		            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 19747: //afro
		            return new Object[] { Integer.valueOf(1), "Donator points @red@($1.0) - 50%" };
		          case 19706: //investigator top
		          case 19707: //investigator bottom
		          case 19708: //investigator hat
		            return new Object[] { Integer.valueOf(2), "Donator points @red@($2.0)" };
		          case 19776: //Swanky boots
		            return new Object[] { Integer.valueOf(2), "Donator points @red@($2.0)" };
		         }
		       } else if (shop == 52) {
		          switch (item) {
		          case 11995: //chaos ele
			            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0) - 50%" };
		          case 11993: //Kqueen
			            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0)" };
		          case 11987: //Nex
		          case 12001: //corp
		            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0)" };
		          case 11992: //tds
		          case 11997: //graador
		          case 12002: //kree
		          case 12003: //kril
		          case 12004: //zilyana
		            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0)" };
		          case 11989: //phoenix
		            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0)" };
		          case 11984: //ice worm
		          case 11985: //desert worm
		          case 11986: //jungle worm
		          case 11988: //bandos avatar
		            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0)" };
		          case 11990: //rex
		          case 11991: //frost
		          case 11994: //slashbash
		          case 11996: //kbd
		          case 12005: //supreme
		          case 12006: //prime
		            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0)" };
		          case 11978: //jad
			            return new Object[] { Integer.valueOf(10), "Donator points @red@($15.0)" };
		          case 11979: //black drag
		          case 11981: //bvlue drag
		          case 11983: //green drag
		          case 11982: //baby blue
		            return new Object[] { Integer.valueOf(10), "Donator points @red@($10.0)" };
		          case 6500: //charm imp
		            return new Object[] { Integer.valueOf(7), "Donator points @red@($7.0)" };
		          case 19111: //rhino cape
		          case 11846: //ahrim
		          case 11848: //verac
		          case 11850: //dharok
		          case 11852: //torag
		          case 11854: //karil
		          case 11856: //guthan
		          case 13262: //d defender
		          case 15018: //seers
		          case 15019: //archer
		          case 15020: //warrior
		          case 15220: //serker
		            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          case 2572: //ROW
			            return new Object[] { Integer.valueOf(5), "Donator points @red@($5.0)" };
		          }
		        } else if (shop == PKING_REWARDS_STORE) {
		          switch (item) {
		          case 2579: 
		          case 6889: 
		          case 6914: 
		          case 6918: 
		            return new Object[] {Integer.valueOf(5), "Pk points" };
		          case 6916: 
		            return new Object[] {Integer.valueOf(8), "Pk points" };
		          case 6924: 
		            return new Object[] {Integer.valueOf(6), "Pk points" };
		          case 6920: 
		          case 6922: 
		            return new Object[] {Integer.valueOf(4), "Pk points" };
		          case 2581: 
		          case 11730: 
		            return new Object[] {Integer.valueOf(25), "Pk points" };
		          case 2577: 
		            return new Object[] {Integer.valueOf(20), "Pk points" };
		          case 15486: 
		          case 19111: 
		            return new Object[] {Integer.valueOf(30), "Pk points" };
		          case 13879: 
		          case 13883: 
		          case 15243: 
		          case 15332: 
		            return new Object[] {Integer.valueOf(1), "Pk points" };
		          case 15241:
		            return new Object[] {Integer.valueOf(20), "Pk points" };
		          case 10547: 
		          case 10548: 
		          case 10551: 
		            return new Object[] {Integer.valueOf(12), "Pk points" };
		          case 4151: 
		          case 6570: 
		          case 11235: 
		          case 13262: 
		          case 20072: 
		            return new Object[] {Integer.valueOf(8), "Pk points" };
		          case 11696: 
		          case 11698: 
		          case 11700: 
		          case 14484: 
		          case 19780: 
		            return new Object[] {Integer.valueOf(50), "Pk points" };
		          case 11728: 
		          case 15018: 
		          case 15020: 
		          case 15220: 
		            return new Object[] {Integer.valueOf(20), "Pk points" };
		          case 11694: 
		            return new Object[] {Integer.valueOf(55), "Pk points" };
		        }
		    } else if(shop == 55) {
				switch(item) {
				case 4587:
					return new Object[]{300, "ironman points"};
				case 1333:
					return new Object[]{100, "ironman points"};
				case 4675:
					return new Object[]{600, "ironman points"};
				case 4131:
					return new Object[]{300, "ironman points"};
				case 9185:
					return new Object[]{400, "ironman points"};
				case 15259:
					return new Object[]{1600, "ironman points"};
				case 1381:
					return new Object[]{50, "ironman points"};
				case 1383:
					return new Object[]{50, "ironman points"};
				case 1385:
					return new Object[]{50, "ironman points"};
				case 1387:
					return new Object[]{50, "ironman points"};
				case 4089:
					return new Object[]{250, "ironman points"};
				case 4091:
					return new Object[]{350, "ironman points"};
				case 4093:
					return new Object[]{350, "ironman points"};
				case 4095:
					return new Object[]{200, "ironman points"};
				case 4097:
					return new Object[]{200, "ironman points"};
				case 6:
					return new Object[]{400, "ironman points"};
				case 8:
					return new Object[]{400, "ironman points"};
				case 10:
					return new Object[]{400, "ironman points"};
				case 12:
					return new Object[]{400, "ironman points"};
				case 10499:
					return new Object[]{150, "ironman points"};
				case 3749:
					return new Object[]{200, "ironman points"};
				case 6328:
					return new Object[]{100, "ironman points"};
				case 9245:
					return new Object[]{10, "ironman points"};
				case 2:
					return new Object[]{3, "ironman points"};
				case 2550:
					return new Object[]{10, "ironman points"};
				case 2570:
					return new Object[]{15, "ironman points"};
				case 11118:
					return new Object[]{100, "ironman points"};
				case 10828:
					return new Object[]{100, "ironman points"};
				case 3755:
					return new Object[]{80, "ironman points"};
				case 15272:
					return new Object[]{10, "ironman points"};
				case 2442:
					return new Object[]{10, "ironman points"};
				case 2436:
					return new Object[]{10, "ironman points"};
				case 2440:
					return new Object[]{10, "ironman points"};
				case 2444:
					return new Object[]{10, "ironman points"};
				case 3040:
					return new Object[]{10, "ironman points"};
				case 6739:
					return new Object[]{1300, "ironman points"};
				case 1359:
					return new Object[]{120, "ironman points"};
				case 9075:
					return new Object[]{3, "ironman points"};
				}
			}  else if(shop == ENERGY_FRAGMENT_STORE) {
				switch(item) {
				case 5509:
					return new Object[]{400, "energy fragments"};
				case 5510:
					return new Object[]{750, "energy fragments"};
				case 5512:
					return new Object[]{1100, "energy fragments"};
				}
			} else if(shop == AGILITY_TICKET_STORE) {
				switch(item) {
				case 14936:
				case 14938:
					return new Object[]{60, "agility tickets"};
				case 10941:
				case 10939:
				case 10940:
				case 10933:
					return new Object[]{20, "agility tickets"};
				case 13661:
					return new Object[]{100, "agility tickets"};
				}
			} else if(shop == LOYALTY_STORE) {
				switch(item) {
				case 15300:
					return new Object[]{10000, "loyalty points"};
				case 3016:
					return new Object[]{5000, "loyalty points"};
				case 15065:
					return new Object[]{80000, "loyalty points"};
				case 6856:
				case 6858:
				case 6862:
				case 6860:
					return new Object[]{200000, "loyalty points"};
				case 6959:
					return new Object[]{125000, "loyalty points"};
				case 15069:
				case 15071:
					return new Object[]{130000, "loyalty points"};
				case 6885:
				case 6886:
				case 6887:
					return new Object[]{150000, "loyalty points"};
				case 20064:
					return new Object[]{180000, "loyalty points"};
				case 20077:
					return new Object[]{250000, "loyalty points"};
				case 15352:
					return new Object[]{350000, "loyalty points"};
				case 14742:
					return new Object[]{700000, "loyalty points"};
				case 15215:
					return new Object[]{1500000, "loyalty points"};
				}
			}else if(shop == GRAVEYARD_STORE) {
				switch(item) {
				case 18337:
					return new Object[]{350, "zombie fragments"};
				case 10551:
					return new Object[]{500, "zombie fragments"};
				case 10548:
				case 10549:
				case 10550:
					return new Object[]{200, "zombie fragments"};
				case 7592:
				case 7593:
				case 7594:
				case 7595:
				case 7596:
					return new Object[]{25, "zombie fragments"};
				case 15241:
					return new Object[]{500, "zombie fragments"};
				case 15243:
					return new Object[]{2, "zombie fragments"};
				}
			} else if(shop == TOKKUL_EXCHANGE_STORE) {
				switch(item) {
				case 11978:
					return new Object[]{400000, "tokkul"};
				case 438:
				case 436:
					return new Object[]{10, "tokkul"};
				case 440:
					return new Object[]{25, "tokkul"};
				case 453:
					return new Object[]{30, "tokkul"};
				case 442:
					return new Object[]{30, "tokkul"};
				case 444:
					return new Object[]{40, "tokkul"};
				case 447:
					return new Object[]{70, "tokkul"};
				case 449:
					return new Object[]{120, "tokkul"};
				case 451:
					return new Object[]{250, "tokkul"};
				case 1623:
					return new Object[]{20, "tokkul"};
				case 1621:
					return new Object[]{40, "tokkul"};
				case 1619:
					return new Object[]{70, "tokkul"};
				case 1617:
					return new Object[]{150, "tokkul"};
				case 1631:
					return new Object[]{1600, "tokkul"};
				case 6571:
					return new Object[]{50000, "tokkul"};
				case 11128:
					return new Object[]{22000, "tokkul"};
				case 6522:
					return new Object[]{20, "tokkul"};
				case 6524:
				case 6523:
				case 6526:
					return new Object[]{5000, "tokkul"};
				case 6528:
				case 6568:
					return new Object[]{800, "tokkul"};
				}
			} else if (shop == DUNGEONEERING_STORE) {
		        switch (item)
		        {
		        case 11137:
		        	return new Object[] { Integer.valueOf(250000), "Dungeoneering tokens" };
		        case 18349: 
		        case 18351: 
		        case 18353: 
		        case 18355: 
		        case 18357: 
		        case 18359: 
		        case 18361: 
		        case 18363: 
		          return new Object[] { Integer.valueOf(200000), "Dungeoneering tokens" };
		        case 19669:
			          return new Object[] { Integer.valueOf(150000), "Dungeoneering tokens" };
		        case 18335: 
		          return new Object[] { Integer.valueOf(100000), "Dungeoneering tokens" };
		        }
		    } else if (shop == PRESTIGE_STORE) {
		          switch (item) {
		          case 14018: 
		            return new Object[] { Integer.valueOf(90), "Prestige points" };
		          case 19335: 
		            return new Object[] { Integer.valueOf(30), "Prestige points" };
		          case 15018: 
		          case 15019: 
		          case 15020: 
		          case 15220: 
		          case 20157: 
		            return new Object[] { Integer.valueOf(20), "Prestige points" };
		          case 20000: 
		          case 20001: 
		          case 20002: 
		            return new Object[] { Integer.valueOf(50), "Prestige points" };
		          case 4084: 
		            return new Object[] { Integer.valueOf(80), "Prestige points" };
		          case 13848: 
		          case 13850: 
		          case 13851: 
		          case 13853: 
		          case 13854: 
		          case 13855: 
		          case 13856: 
		          case 13857: 
		          case 13852: 
		            return new Object[] { Integer.valueOf(5), "Prestige points" };
		          case 10400: 
		          case 10402: 
		          case 10404: 
		          case 10406: 
		          case 10408: 
		          case 10410: 
		          case 10412: 
		          case 10414: 
		          case 10416: 
		          case 10418: 
		            return new Object[] { Integer.valueOf(2), "Prestige points" };
		          case 14595: 
		          case 14603: 
		            return new Object[] { Integer.valueOf(5), "Prestige points" };
		          case 14602: 
		          case 14605: 
		            return new Object[] { Integer.valueOf(5), "Prestige points" };
		          case 15040: 
			        return new Object[] { Integer.valueOf(50), "Prestige points" };
		          case 15041: 
				    return new Object[] { Integer.valueOf(50), "Prestige points" };
		          case 15042: 
				    return new Object[] { Integer.valueOf(40), "Prestige points" };
		          case 15043: 
				    return new Object[] { Integer.valueOf(30), "Prestige points" };
		          case 15044: 
				    return new Object[] { Integer.valueOf(30), "Prestige points" };
		        }
		     } else if (shop == SLAYER_STORE) {
		          switch (item) {
		          case 13263: 
		            return new Object[] { Integer.valueOf(250), "Slayer points" };
		          case 13281: 
		            return new Object[] { Integer.valueOf(5), "Slayer points" };
		          case 10887: 
		          case 11730: 
		          case 15241: 
		          case 15403: 
		            return new Object[] { Integer.valueOf(300), "Slayer points" };
		          case 4151: 
		          case 11235: 
		          case 15486: 
		            return new Object[] { Integer.valueOf(250), "Slayer points" };
		          case 15243: 
		            return new Object[] { Integer.valueOf(3), "Slayer points" };
		          case 10551: 
		            return new Object[] { Integer.valueOf(200), "Slayer points" };
		          case 2572: 
		            return new Object[] { Integer.valueOf(350), "Slayer points" };
		          }
		        } else if (shop == RECIPE_FOR_DISASTER_STORE) {
		        	switch(item) {
		        	case 7453:
		        		return new Object[] { Integer.valueOf(10000), "Coins" };
		        	case 7454:
		        		return new Object[] { Integer.valueOf(20000), "Coins" };
		        	case 7455:
		        		return new Object[] { Integer.valueOf(30000), "Coins" };
		        	case 7456:
		        		return new Object[] { Integer.valueOf(40000), "Coins" };
		        	case 7457:
		        		return new Object[] { Integer.valueOf(50000), "Coins" };
		        	case 7458:
		        		return new Object[] { Integer.valueOf(60000), "Coins" };
		        	case 7459:
		        		return new Object[] { Integer.valueOf(70000), "Coins" };
		        	case 7460:
		        		return new Object[] { Integer.valueOf(80000), "Coins" };
		        	case 7461:
		        		return new Object[] { Integer.valueOf(90000), "Coins" };
		        	case 7462:
		        		return new Object[] { Integer.valueOf(100000), "Coins" };
		        	} 		        
		        } else if (shop == GAMBLING_STORE) {
		        	switch(item) {
		        	case 15084:
		        		return new Object[] { Integer.valueOf(500000000), "Coins" };
		        	case 299:
		        		return new Object[] { Integer.valueOf(250000000), "Coins" };
		        	}
		        } else if (shop == MEMBERS_STORE) {
		        	switch(item) {
		        	case 10942:
		        		return new Object[] { Integer.valueOf(500000000), "Coins" };
		        	case 10934:
		        		return new Object[] { Integer.valueOf(1000000000), "Coins" };
		        	case 10935:
		        		return new Object[] { Integer.valueOf(1500000000), "Coins" };
		        	case 10943:
		        		return new Object[] { Integer.valueOf(2147000000), "Coins" };
		        	}
		        }

			return null;
		}
	}

	public static final int INTERFACE_ID = 3824;
	public static final int ITEM_CHILD_ID = 3900;
	public static final int NAME_INTERFACE_CHILD_ID = 3901;
	public static final int INVENTORY_INTERFACE_ID = 3823;
	public static final int RECIPE_FOR_DISASTER_STORE = 36;
	private static final int VOTING_REWARDS_STORE = 27;
	private static final int PKING_REWARDS_STORE = 26;
	private static final int ENERGY_FRAGMENT_STORE = 33;
	private static final int AGILITY_TICKET_STORE = 39;
	private static final int GRAVEYARD_STORE = 42;
	private static final int TOKKUL_EXCHANGE_STORE = 43;
	private static final int SKILLCAPE_STORE_1 = 8;
	private static final int SKILLCAPE_STORE_2 = 9;
	private static final int SKILLCAPE_STORE_3 = 10;
	private static final int GAMBLING_STORE = 41;
	private static final int DUNGEONEERING_STORE = 44;
	private static final int PRESTIGE_STORE = 46;
	private static final int SLAYER_STORE = 47;
	private static final int LOYALTY_STORE = 54;
	public static final int GENERAL_STORE = 12;
	
	public static final int RUNES = 0;
	public static final int MAGIC_EQUIPMENT = 1;
	public static final int AMMUNITION = 2;
	public static final int RANGED_EQUIPMENT = 3;
	public static final int MELEE_EQUIPMENT = 4;
	public static final int MELEE_WEAPONS = 5;
	public static final int CONSUMABLES = 6;
	public static final int JEWELRIES_STORE = 7;
	public static final int MERCHANT_STORE = 11;
	public static final int MINING_STORE = 13;
	public static final int WOODCUT_STORE = 14;
	public static final int FIREMAKING_STORE = 15;
	public static final int COOKING_STORE = 16;
	public static final int PRAYER_STORE = 17;
	public static final int FISHING_STORE = 18;
	public static final int PURE_PVP = 19;
	public static final int COSTUME_STORE = 20;
	public static final int FARMING_STORE = 21;
	public static final int SUMMOMING1 = 22;
	public static final int SUMMOMING2 = 23;
	public static final int HERBLORE_STORE = 30;
	public static final int POTIONS_STORE = 31;
	public static final int RUNECRAFTING_STORE = 32;
	public static final int CRAFTING_STORE = 34;
	public static final int ARMOUR_STORE = 35;
	public static final int HUNTER_STORE = 38;
	public static final int MEMBERS_STORE = 58;
}