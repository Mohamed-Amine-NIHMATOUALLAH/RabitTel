USE rabittel_lignes;
SELECT 'All VPN_ADSL lines in parent table:' as info;
SELECT HEX(id) as id_hex, line_number, line_type, line_status FROM line WHERE line_type = 'VPN_ADSL';
SELECT 'vpn_lines rows:' as info;
SELECT HEX(line_id) as line_id_hex, bandwidth, ip_address FROM vpn_lines;
SELECT 'Count VPN in line table:' as info;
SELECT COUNT(*) FROM line WHERE line_type = 'VPN_ADSL';
SELECT 'Count vpn_lines rows:' as info;
SELECT COUNT(*) FROM vpn_lines;
