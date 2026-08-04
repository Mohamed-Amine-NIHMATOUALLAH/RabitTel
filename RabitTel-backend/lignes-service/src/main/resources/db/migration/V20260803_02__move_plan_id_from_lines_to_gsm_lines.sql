-- =========================================================================
-- MIGRATION V20260803_02
-- Règle métier : SEULS les GSM Pro (gsm_lines) ont une association Forfait (Plan).
-- Tous les autres types de ligne (FTTH, RTC, Data, 4G, VPN4G) perdent cette relation.
-- Action : déplacer la colonne plan_id de `lines` (table mère) vers `gsm_lines`.
-- =========================================================================

-- -------------------------------------------------------------------------
-- ÉTAPE 0 (DIAGNOSTIC) : Identifier le NOM EXACT de la contrainte FK sur lines.plan_id
-- Exécute cette requête seule en premier pour connaître le nom de la FK :
--   SELECT CONSTRAINT_NAME
--   FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
--   WHERE TABLE_SCHEMA = DATABASE()
--     AND TABLE_NAME = 'lines'
--     AND COLUMN_NAME = 'plan_id'
--     AND REFERENCED_TABLE_NAME IS NOT NULL;
--
-- Ensuite remplace `REPLACE_WITH_FK_NAME` ci-dessous par la valeur renvoyée.
-- -------------------------------------------------------------------------

-- ÉTAPE 1 : Supprimer la Foreign Key existante sur lines.plan_id
ALTER TABLE `lines` DROP FOREIGN KEY `REPLACE_WITH_FK_NAME`;

-- ÉTAPE 2 : Supprimer l'INDEX éventuel sur lines.plan_id (si pas auto-drop par la FK)
DROP INDEX IF EXISTS `plan_id` ON `lines`;

-- ÉTAPE 3 : Supprimer la colonne plan_id de la table mère `lines`
ALTER TABLE `lines` DROP COLUMN `plan_id`;

-- ÉTAPE 4 : Ajouter la colonne plan_id DANS gsm_lines (seul type avec Forfait)
-- Note : BINARY(16) correspond au format UUID stocké par JPA.
ALTER TABLE `gsm_lines`
    ADD COLUMN `plan_id` BINARY(16) NOT NULL;

-- ÉTAPE 5 : Ajouter la contrainte Foreign Key vers plan(id)
ALTER TABLE `gsm_lines`
    ADD CONSTRAINT `fk_gsm_lines_plan`
        FOREIGN KEY (`plan_id`) REFERENCES `plan`(`id`);

-- ÉTAPE 6 : Ajouter un INDEX sur gsm_lines.plan_id pour optimiser les recherches/COUNT
CREATE INDEX `idx_gsm_lines_plan_id` ON `gsm_lines`(`plan_id`);
