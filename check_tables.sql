USE rabittel_lignes;
SHOW TABLES;
SELECT 'line table:' as info;
SELECT id, line_number, line_type, line_status FROM line LIMIT 20;
SELECT 'vpn_lines join:' as info;
SELECT l.id, l.line_number, l.line_type, v.bandwidth 
FROM line l 
LEFT JOIN vpn_lines v ON l.id = v.line_id 
WHERE l.line_type = 'VPN_ADSL';
