<?php
//    POS-Tech API
//
//    Copyright (C) 2012 Scil (http://scil.coop)
//
//    This file is part of POS-Tech.
//
//    POS-Tech is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    POS-Tech is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with POS-Tech.  If not, see <http://www.gnu.org/licenses/>.

require_once(dirname(dirname(__FILE__)) . "/services/CategoriesService.php");
require_once(dirname(dirname(__FILE__)) . "/services/TaxesService.php");
require_once(dirname(dirname(__FILE__)) . "/services/AttributesService.php");
require_once(dirname(dirname(__FILE__)) . "/models/Product.php");
require_once(dirname(dirname(__FILE__)) . "/PDOBuilder.php");

class ProductsService {

    private static function buildDBLightPrd($db_prd) {
        return ProductLight::__build($db_prd['ID'], $db_prd['REFERENCE'],
                                     $db_prd['NAME'], $db_prd['PRICESELL'],
                                     $db_prd['ISCOM'], $db_prd['ISSCALE'],
                                     $db_prd['CODE'], $db_prd['PRICEBUY']);
    }

    private static function buildDBPrd($db_prd) {
        $cat = CategoriesService::get($db_prd['CATEGORY']);
        $tax_cat = TaxesService::get($db_prd['TAXCAT']);
        $attr = AttributesService::get($db_prd['ATTRIBUTES']);
        return Product::__build($db_prd['ID'], $db_prd['REFERENCE'],
                                $db_prd['NAME'], $db_prd['PRICESELL'],
                                $cat, $tax_cat, $db_prd['ISCOM'],
                                $db_prd['ISSCALE'], $db_prd['PRICEBUY'],
                                $attr, $db_prd['CODE']);
    }

    static function getAll($full = false) {
        $prds = array();
        $pdo = PDOBuilder::getPDO();
        $sql = "SELECT * FROM PRODUCTS";
        foreach ($pdo->query($sql) as $db_prd) {
            if ($full) {
                $prd = ProductsService::buildDBPrd($db_prd);
            } else {
                $prd = ProductsService::buildDBLightPrd($db_prd);
            }
            $prds[] = $prd;
        }
        return $prds;
    }

    static function get($id) {
        $pdo = PDOBuilder::getPDO();
        $stmt = $pdo->prepare("SELECT * FROM PRODUCTS WHERE ID = :id");
        if ($stmt->execute(array(':id' => $id))) {
            if ($row = $stmt->fetch()) {
                $prd = ProductsService::buildDBPrd($row);
                return $prd;
            }
        }
        return null;
    }

    static function update($prd) {
        $pdo = PDOBuilder::getPDO();
        $attr_id = null;
        if ($prd->attributes_set != null) {
            $attr_id = $prd->attributes_set->id;
        }
        $code = "";
        if ($prd->barcode != null) {
            $code = $prd->barcode;
        }
        $stmt = $pdo->prepare("UPDATE PRODUCTS SET REFERENCE = :ref, "
                              . "CODE = :code, NAME = :name, PRICEBUY = :buy, "
                              . "PRICESELL = :sell, CATEGORY = :cat, "
                              . "TAXCAT = :tax, ATTRIBUTESET_ID = :attr, "
                              . "ISCOM = :com, ISSCALE = :scale "
                              . "WHERE ID = :id");
        return $stmt->execute(array(':ref' => $prd->reference,
                                    ':code' => $code,
                                    ':name' => $prd->label,
                                    ':buy' => $prd->price_buy,
                                    ':sell' => $prd->price_sell,
                                    ':cat' => $prd->category->id,
                                    ':tax' => $prd->tax_cat->id,
                                    ':attr' => $attr_id,
                                    ':com' => $prd->visible,
                                    ':scale' => $prd->scaled,
                                    ':id' => $prd->id));
    }
    
    static function create($prd) {
        $pdo = PDOBuilder::getPDO();
        $id = md5(time() . rand());
        $attr_id = null;
        if ($prd->attributes_set != null) {
            $attr_id = $prd->attributes_set->id;
        }
        $code = "";
        if ($prd->barcode != null) {
            $code = $prd->barcode;
        }
        $stmt = $pdo->prepare("INSERT INTO PRODUCTS (ID, REFERENCE, CODE, NAME, "
                              . "PRICEBUY, PRICESELL, CATEGORY, TAXCAT, "
                              . "ATTRIBUTESET_ID, ISCOM, ISSCALE) VALUES "
                              . "(:id, :ref, :code, :name, :buy, :sell, :cat, "
                              . ":tax, :attr, :com, :scale)");
        return $stmt->execute(array(':ref' => $prd->reference,
                                    ':code' => $code,
                                    ':name' => $prd->label,
                                    ':buy' => $prd->price_buy,
                                    ':sell' => $prd->price_sell,
                                    ':cat' => $prd->category->id,
                                    ':tax' => $prd->tax_cat->id,
                                    ':attr' => $attr_id,
                                    ':com' => $prd->visible,
                                    ':scale' => $prd->scaled,
                                    ':id' => $id));
    }
    
    static function delete($id) {
        $pdo = PDOBuilder::getPDO();
        $stmt = $pdo->prepare("DELETE FROM PRODUCTS WHERE ID = :id");
        return $stmt->execute(array(':id' => $id));
    }
}

?>
