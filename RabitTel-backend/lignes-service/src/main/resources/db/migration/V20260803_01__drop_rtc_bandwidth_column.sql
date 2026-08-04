-- ===========================================================================
-- Migration: V20260803_01
-- Objet    : Suppression de la colonne `bandwidth` de la table `rtc_lines`
-- Raison   : Une ligne RTC (Réseau Téléphonique Commuté) est une ligne
--            vocale traditionnelle et n'a PAR NATURE pas de notion de
--            débit / bande passante. Ce champ a été ajouté par erreur
--            et cause l'erreur suivante à l'INSERT :
--            "Field 'bandwidth' doesn't have a default value"
--
-- ATTENTION : Ce fichier n'est PAS pris en charge automatiquement
--             (aucun Flyway / Liquibase n'est configuré).
--             Il doit être exécuté MANUELLEMENT sur la base `RabitTel_Lignes`.
-- ===========================================================================

-- Étape 1 : Sécurité — rendre la colonne nullable avant de la dropper
--          (évite les blocages si des lignes y font référence)
ALTER TABLE `rtc_lines`
    MODIFY COLUMN `bandwidth` VARCHAR(20) NULL DEFAULT NULL;

-- Étape 2 : Suppression définitive de la colonne
ALTER TABLE `rtc_lines`
    DROP COLUMN `bandwidth`;

-- Vérification (optionnelle) :
-- DESCRIBE `rtc_lines`;
