var express = require('express');
var bodyParser = require("body-parser");
var MongoClient = require('mongodb').MongoClient;

const sqlite3 = require('sqlite3').verbose();
//let db = new sqlite3.Database('./CoffeeDB.db');

const router = express.Router();
const app = express();
const port = 3000;

const url = 'mongodb://localhost';
const dbName = 'CoffeeDB';



app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());

app.use(router);

router.get('/', (req, res) => {
	res.send('Returned from Nodejs server.')
})

router.get('/all', (req, res) => {
	let db = new sqlite3.Database('./CoffeeDB.db');

	let selectAllSQL = `SELECT "checkpoint_name", "checkpoint_id" FROM "roast_checkpoint"
						UNION
						SELECT "bean_name", "bean_id" FROM "bean"`;
	
	db.all(selectAllSQL, [], (err, rows) => {
		if(err)
			throw err;
		
		res.send(rows);
	});
})
router.get('/allBeans', (req, res) => {
	let db = new sqlite3.Database('./CoffeeDB.db');
	
	let selectAllSQL = `SELECT * FROM "bean"`
	db.all(selectAllSQL, [], (err, rows) => {
		if(err)
			throw err;
	
		res.send(rows);
	});
})
router.get('/allCheckpoints', (req, res) => {
	let db = new sqlite3.Database('./CoffeeDB.db');
	
	let selectAllSQL = `SELECT * FROM "roast_checkpoint"`
	db.all(selectAllSQL, [], (err, rows) => {
		if(err)
			throw err;
	
		res.send(rows);
	});
})
router.get('/allRoasts', (req, res) => {
	let db = new sqlite3.Database('./CoffeeDB.db');
	
	//let selectAllSQL = `SELECT r.*, rc.* FROM "roast_profile" r LEFT JOIN "checkpoints" c ON r.roast_profile_id=c.id LEFT JOIN "roast_checkpoint" rc ON c.checkpoint=rc.checkpoint_id`;
	let selectAllSQL = `SELECT * FROM "roast_profile"`;
	db.all(selectAllSQL, [], (err, rows) => {
		if(err)
			throw err;
	
		res.send(rows);
	});
})
router.get('/allBlends', (req, res) => {
	let db = new sqlite3.Database('./CoffeeDB.db');
	
	let selectAllSQL = `SELECT * FROM "blend"`;
	db.all(selectAllSQL, [], (err, rows) => {
		if(err)
			throw err;
	
		res.send(rows);
	});
})

router.get('/bean/:beanid', (req, res) => {
	//res.send('Return bean with id:'+req.params['beanid'])
	let db = new sqlite3.Database('./CoffeeDB.db');
	
	var sql = `SELECT * FROM "bean" WHERE "bean_id"=`+req.params['beanid'];
	db.get(sql, [], function(err, row){
	if(err){
		return console.log(err.message);
	}
		res.send(row);
	});
})
router.get('/roast_blend/:blendid', (req, res) => {
	let db = new sqlite3.Database('./CoffeeDB.db');
	
	let selectAllSQL = `SELECT * FROM "roast_blend" WHERE "blend_id"=`+req.params['blendid'];
	db.all(selectAllSQL, [], (err, rows) => {
		if(err)
			throw err;
	
		res.send(rows);
	});
})
router.get('/blend/:blendid', (req, res) => {
	let db = new sqlite3.Database('./CoffeeDB.db');
	
	let selectSQL = `SELECT * FROM "blend" WHERE "blend_id"=`+req.params['blendid'];
	db.get(selectSQL, [], function(err, row){
	if(err){
		return console.log(err.message);
	}
		res.send(row);
	});
})
router.get('/checkpoints/:roastId', (req, res) => {
	//res.send('Return checkpoint with id:'+req.params['checkpointid'])
	let db = new sqlite3.Database('./CoffeeDB.db');
	
	let selectAllSQL = `SELECT * FROM "checkpoints" WHERE "id"=`+req.params['roastId'];
	db.all(selectAllSQL, [], (err, rows) => {
		if(err)
			throw err;
	
		res.send(rows);
	});
})
router.get('/checkpoint/:checkpointid', (req, res) => {
	let db = new sqlite3.Database('./CoffeeDB.db');
	
	let selectAllSQL = `SELECT * FROM "roast_checkpoint" WHERE "checkpoint_id"=`+req.params['checkpointid'];
	db.get(selectAllSQL, [], function(err, row){
	if(err){
		return console.log(err.message);
	}
		res.send(row);
	});
})
router.get('/roast/:roastid', (req, res) => {
	let db = new sqlite3.Database('./CoffeeDB.db');
	
	var sql = `SELECT * FROM "roast_profile" WHERE "roast_profile_id"=`+req.params['roastid'];
	db.get(sql, [], function(err, row){
	if(err){
		return console.log(err.message);
	}
		res.send(row);
	});
})

router.post('/bean', (req,res) => {
	let db = new sqlite3.Database('./CoffeeDB.db');
	
	let beanValues = [null, req.body.name, req.body.origin, req.body.farm, req.body.altitude, req.body.process, req.body.flavours, req.body.body, req.body.acidity, req.body.dryingMethod, req.body.pricePerPound];
	let placeholders = beanValues.map((beanValues) => '(?)').join(',');

	var sql = `
		INSERT INTO "bean"("bean_id",
				"bean_name", 
				"bean_origin", 
				"bean_farm", 
				"bean_altitude", 
				bean_process_style, 
				bean_flavour, 
				bean_body, 
				bean_acidity, 
				bean_drying_method, 
				bean_price_per_pound) 
		VALUES(`+placeholders+')';
	
	db.run(sql, beanValues, function(err){
	if(err){
		return console.log(err.message);
	}
		console.log(`A row has been inserted with id: ${this.lastID}`);
		res.send('ID:'+this.lastID);
	});
});
router.post('/blend', (req,res) => {
	let db = new sqlite3.Database('./CoffeeDB.db');

	console.log('Post request blend: %j', req.body.name);
	
	let blendValues = [null, req.body.description, req.body.name];

	let placeholders = blendValues.map((blendValues) => '?').join(',');

	var sql = `INSERT INTO "blend"("blend_id",
					"blend_description",
					"blend_name")
		VALUES(`+placeholders+')';

	db.run(sql, blendValues, function(err){
		if(err){
			return console.log(err.message);
		}
		console.log(`A blend has been added with id:${this.lastID}`);
		res.send('ID:'+this.lastID);
	});
});
router.post('/roast_blend', (req,res) => {
	let db = new sqlite3.Database('./CoffeeDB.db');

	console.log('Post request roast_blend.');
	
	let blendValues = [req.body.roast_profile_id, req.body.blend_id];

	let placeholders = blendValues.map((blendValues) => '?').join(',');

	var sql = `INSERT INTO "roast_blend"("roast_profile_id",
					"blend_id")
		VALUES(`+placeholders+')';

	db.run(sql, blendValues, function(err){
		if(err){
			return console.log(err.message);
		}
		console.log(`A roast_blend association has been added.`);
		res.send('OK');
	});
});
router.post('/checkpoint', (req,res) => {
	let db = new sqlite3.Database('./CoffeeDB.db');

	//console.log('Post request %j', req.body);
	console.log('Post request checkpoint: %j', req.body.name);
	
	let checkpointValues = [null, req.body.name, req.body.trigger, req.body.time, req.body.temperature];

	let placeholders = checkpointValues.map((checkpointValues) => '?').join(',');

	var sql = `INSERT INTO "roast_checkpoint"("checkpoint_id",
					"checkpoint_name",
					"check_trigger",
					"time",
					"temperature")
		VALUES(`+placeholders+')';

	db.run(sql, checkpointValues, function(err){
		if(err){
			return console.log(err.message);
		}
		console.log(`A checkpoint has been added with id:${this.lastID}`);
		res.send('ID:'+this.lastID);
	});
});
router.post('/roast', (req,res) => {
	let db = new sqlite3.Database('./CoffeeDB.db');
	
	console.log('Post request roast: %j', req.body.name);
	
	let roastValues = [null, req.body.name, req.body.roastLevel, req.body.bean, req.body.chargeTemp, req.body.dropTemp, req.body.flavour];
	let placeholders = roastValues.map((roastValues) => '?').join(',');
	
	let sql = `INSERT INTO "roast_profile"(
							"roast_profile_id",
							"roast_profile_name",
							"roast",
							"bean_id",
							"charge_temp",
							"drop_temp",
							"flavour")
					VALUES(`+placeholders+`)`;
							
	db.run(sql, roastValues, function(err){
		if(err)
			return console.log(err.message);
		console.log(`A roast has been added with id:${this.lastID}`);
		res.send('ID:'+this.lastID);
	});
});
router.post('/checkpoints', (req,res) => {
	let db = new sqlite3.Database('./CoffeeDB.db');
	
	console.log('Post request checkpoints');
	
	let checkpointsValues = [req.body.roastId, req.body.checkpointId];
	let placeholders = checkpointsValues.map((checkpointsValues) => '?').join(',');
	
	let sql = `INSERT INTO "checkpoints"(
							"id",
							"checkpoint")
					VALUES(`+placeholders+`)`;
					
	db.run(sql, checkpointsValues, function(err){
		if(err)
			return console.log(err.message);
		console.log(`A roast-checkpoint association has been added.`);
		res.send(`OK`);
	});
	
});



app.listen(port, () => {
	
	console.log('Example app listening at http://localhost:'+port);
})
