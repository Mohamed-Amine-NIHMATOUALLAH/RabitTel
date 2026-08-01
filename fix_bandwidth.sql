USE rabittel_lignes;
SELECT 'Before fix:' as info;
SELECT line_id, bandwidth FROM vpn_lines;
UPDATE vpn_lines SET bandwidth = 0 WHERE bandwidth IS NULL;
SELECT 'After fix:' as info;
SELECT line_id, bandwidth FROM vpn_lines;
