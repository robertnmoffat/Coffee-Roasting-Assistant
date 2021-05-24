const sqlite3 = require('sqlite3').verbose();

let db = new sqlite3.Database('./CoffeeDB.db');

var TABLE_BLEND = "blend";//
var TABLE_ROAST_PROFILE = "roast_profile";//
var TABLE_ROAST_BLEND = "roast_blend";//
var TABLE_CHECKPOINTS = "checkpoints";
var TABLE_ROAST_CHECKPOINT = "roast_checkpoint";//
var TABLE_BEAN = "bean";//

console.log('Creating bean table...');
db.run(`	
    CREATE TABLE IF NOT EXISTS "${TABLE_BEAN}"(
						"bean_id" INTEGER PRIMARY KEY, 
						"bean_name" TEXT, 
						"bean_origin" TEXT, 
						"bean_farm" TEXT, 
						"bean_altitude" INTEGER, 
						"bean_process_style" TEXT, 
						"bean_flavour" TEXT, 
						"bean_body" TEXT, 
						"bean_acidity" TEXT, 
						"bean_drying_method" TEXT, 
						"bean_price_per_pound" DECIMAL);  
`);	
console.log('bean table created.');

console.log('Creating checkpoint table...');
db.run(`	
    CREATE TABLE IF NOT EXISTS "${TABLE_ROAST_CHECKPOINT}"(
						"checkpoint_id" INTEGER PRIMARY KEY, 
						"checkpoint_name" TEXT, 
						"check_trigger" TEXT, 
						"time" INTEGER, 
						"temperature" INTEGER);  
`);	
console.log('checkpoint table created.');

console.log('Creating blend table...');
db.run(`	
    CREATE TABLE IF NOT EXISTS "${TABLE_BLEND}"(
						"blend_id" INTEGER PRIMARY KEY,
						"blend_description" TEXT,
						"blend_name" TEXT);  
`);	
console.log('blend table created.');

console.log('Creating roast_profile table...');
db.run(`	
    CREATE TABLE IF NOT EXISTS "${TABLE_ROAST_PROFILE}"(
						"roast_profile_id" INTEGER PRIMARY KEY, 
						"roast_profile_name" TEXT, 
						"roast" TEXT,
						"bean_id" INTEGER REFERENCES "bean",
						"charge_temp" INTEGER,
						"drop_temp" INTEGER,
						"flavour" TEXT);  
`);	
console.log('roast_profile table created.');

console.log('Creating ${TABLE_ROAST_BLEND} table...');
db.run(`	
    CREATE TABLE IF NOT EXISTS "${TABLE_ROAST_BLEND}"(
						"roast_profile_id" INTEGER REFERENCES "${TABLE_BLEND}", 
						"blend_id" INTEGER REFERENCES "${TABLE_ROAST_PROFILE}");  
`);	
console.log('${TABLE_ROAST_BLEND} table created.');

console.log('Creating ${TABLE_CHECKPOINTS} table...');
db.run(`	
    CREATE TABLE IF NOT EXISTS "${TABLE_CHECKPOINTS}"(
						"id" INTEGER REFERENCES "${TABLE_ROAST_PROFILE}", 
						"checkpoint" INTEGER REFERENCES "${TABLE_ROAST_CHECKPOINT}");  
`);	
console.log('${TABLE_CHECKPOINTS} table created.');
