# Quick-Harvest
Adds in right-click harvesting to crops and auto replants afterwards.


# API guide

* [How to register a harvester](#how-to-register-a-harvester)
* [Creating a custom harvester](#creating-a-custom-harvester)

## How to register a harvester

To register a harvester you will need to use the `HarvestManager`. This is like a registry for harvesters. Specifically you will need to call `HarvestManager.regsiter()`. This method takes 2 parameters, the first is your harvester and the second is an array of blocks it is effective on.

> **Note**: A Harvester is anything which implements `IHarvester`.

## Creating a custom harvester

In a typical case, when creating a custom harvester you will need to extend the class `HarvesterImpl`. This class provides you will a bunch of methods which you can use when implementing your own harvesters. The only method you must implement yourself in this case is `harvest()`. To see some examples of harvesters, take a look [here](https://github.com/EwyBoy/Quick-Harvest/tree/master/src/main/java/com/ewyboy/quickharvest/harvester).