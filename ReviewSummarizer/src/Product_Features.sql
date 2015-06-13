CREATE TABLE `reviews`.`PRODUCT_FEATURES` (
  `product_id` INT NOT NULL,
  `feature` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`product_id`, `feature`));
