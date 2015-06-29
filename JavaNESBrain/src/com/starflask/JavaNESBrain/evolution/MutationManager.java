package com.starflask.JavaNESBrain.evolution;

import com.starflask.JavaNESBrain.utils.FastMath;

public class MutationManager {

	int Population = 300;
	float DeltaDisjoint = 2.0f;
	float DeltaWeights = 0.4f;
	float DeltaThreshold = 1.0f;

	int StaleSpecies = 15;

	

	int TimeoutConstant = 20;

	int MaxNodes = 1000000;
	
	
public static float sigmoid(int x)
{
	return 2/(1+FastMath.exp(-4.9f*x))-1 ;
}

GenePool pool;
	
public void initializePool()
{
pool = new GenePool();

for i=1,Population do
        basic = basicGenome()
        addToSpecies(basic)
end

initializeRun()

}


public void  initializeRun()
{
savestate.load(Filename);
rightmost = 0 ; 
pool.currentFrame = 0 ;
timeout = TimeoutConstant ; 
clearJoypad(); 

local species = pool.species[pool.currentSpecies] ;
local genome = species.genomes[pool.currentGenome] ; 
generateNetwork(genome) ;
evaluateCurrent();
 
}


public void evaluateCurrent()
{
local species = pool.species[pool.currentSpecies]
local genome = species.genomes[pool.currentGenome]

inputs = getInputs()
controller = evaluateNetwork(genome.network, inputs)

if (controller["P1 Left"] && controller["P1 Right"])
{
        controller["P1 Left"] = false
        controller["P1 Right"] = false
}
if (controller["P1 Up"] && controller["P1 Down"] )
{
        controller["P1 Up"] = false
        controller["P1 Down"] = false
}

joypad.set(controller)
}



	/*
	 * 
	 * 
	 *
	 * 
	 * if pool == nil then
initializePool()
end

 
function generateNetwork(genome)
        local network = {}
        network.neurons = {}
       
        for i=1,Inputs do
                network.neurons[i] = newNeuron()
        end
       
        for o=1,Outputs do
                network.neurons[MaxNodes+o] = newNeuron()
        end
       
        table.sort(genome.genes, function (a,b)
                return (a.out < b.out)
        end)
        for i=1,#genome.genes do
                local gene = genome.genes[i]
                if gene.enabled then
                        if network.neurons[gene.out] == nil then
                                network.neurons[gene.out] = newNeuron()
                        end
                        local neuron = network.neurons[gene.out]
                        table.insert(neuron.incoming, gene)
                        if network.neurons[gene.into] == nil then
                                network.neurons[gene.into] = newNeuron()
                        end
                end
        end
       
        genome.network = network
end
 
function evaluateNetwork(network, inputs)
        table.insert(inputs, 1)
        if #inputs ~= Inputs then
                console.writeline("Incorrect number of neural network inputs.")
                return {}
        end
       
        for i=1,Inputs do
                network.neurons[i].value = inputs[i]
        end
       
        for _,neuron in pairs(network.neurons) do
                local sum = 0
                for j = 1,#neuron.incoming do
                        local incoming = neuron.incoming[j]
                        local other = network.neurons[incoming.into]
                        sum = sum + incoming.weight * other.value
                end
               
                if #neuron.incoming > 0 then
                        neuron.value = sigmoid(sum)
                end
        end
       
        local outputs = {}
        for o=1,Outputs do
                local button = "P1 " .. ButtonNames[o]
                if network.neurons[MaxNodes+o].value > 0 then
                        outputs[button] = true
                else
                        outputs[button] = false
                end
        end
       
        return outputs
end
 
function crossover(g1, g2)
        -- Make sure g1 is the higher fitness genome
        if g2.fitness > g1.fitness then
                tempg = g1
                g1 = g2
                g2 = tempg
        end
 
        local child = newGenome()
       
        local innovations2 = {}
        for i=1,#g2.genes do
                local gene = g2.genes[i]
                innovations2[gene.innovation] = gene
        end
       
        for i=1,#g1.genes do
                local gene1 = g1.genes[i]
                local gene2 = innovations2[gene1.innovation]
                if gene2 ~= nil and math.random(2) == 1 and gene2.enabled then
                        table.insert(child.genes, copyGene(gene2))
                else
                        table.insert(child.genes, copyGene(gene1))
                end
        end
       
        child.maxneuron = math.max(g1.maxneuron,g2.maxneuron)
       
        for mutation,rate in pairs(g1.mutationRates) do
                child.mutationRates[mutation] = rate
        end
       
        return child
end
 

 


 
function disjoint(genes1, genes2)
        local i1 = {}
        for i = 1,#genes1 do
                local gene = genes1[i]
                i1[gene.innovation] = true
        end
 
        local i2 = {}
        for i = 1,#genes2 do
                local gene = genes2[i]
                i2[gene.innovation] = true
        end
       
        local disjointGenes = 0
        for i = 1,#genes1 do
                local gene = genes1[i]
                if not i2[gene.innovation] then
                        disjointGenes = disjointGenes+1
                end
        end
       
        for i = 1,#genes2 do
                local gene = genes2[i]
                if not i1[gene.innovation] then
                        disjointGenes = disjointGenes+1
                end
        end
       
        local n = math.max(#genes1, #genes2)
       
        return disjointGenes / n
end
 
function weights(genes1, genes2)
        local i2 = {}
        for i = 1,#genes2 do
                local gene = genes2[i]
                i2[gene.innovation] = gene
        end
 
        local sum = 0
        local coincident = 0
        for i = 1,#genes1 do
                local gene = genes1[i]
                if i2[gene.innovation] ~= nil then
                        local gene2 = i2[gene.innovation]
                        sum = sum + math.abs(gene.weight - gene2.weight)
                        coincident = coincident + 1
                end
        end
       
        return sum / coincident
end
       
function sameSpecies(genome1, genome2)
        local dd = DeltaDisjoint*disjoint(genome1.genes, genome2.genes)
        local dw = DeltaWeights*weights(genome1.genes, genome2.genes)
        return dd + dw < DeltaThreshold
end
 
function rankGlobally()
        local global = {}
        for s = 1,#pool.species do
                local species = pool.species[s]
                for g = 1,#species.genomes do
                        table.insert(global, species.genomes[g])
                end
        end
        table.sort(global, function (a,b)
                return (a.fitness < b.fitness)
        end)
       
        for g=1,#global do
                global[g].globalRank = g
        end
end
 
function calculateAverageFitness(species)
        local total = 0
       
        for g=1,#species.genomes do
                local genome = species.genomes[g]
                total = total + genome.globalRank
        end
       
        species.averageFitness = total / #species.genomes
end
 
function totalAverageFitness()
        local total = 0
        for s = 1,#pool.species do
                local species = pool.species[s]
                total = total + species.averageFitness
        end
 
        return total
end
 
function cullSpecies(cutToOne)
        for s = 1,#pool.species do
                local species = pool.species[s]
               
                table.sort(species.genomes, function (a,b)
                        return (a.fitness > b.fitness)
                end)
               
                local remaining = math.ceil(#species.genomes/2)
                if cutToOne then
                        remaining = 1
                end
                while #species.genomes > remaining do
                        table.remove(species.genomes)
                end
        end
end
 
function breedChild(species)
        local child = {}
        if math.random() < CrossoverChance then
                g1 = species.genomes[math.random(1, #species.genomes)]
                g2 = species.genomes[math.random(1, #species.genomes)]
                child = crossover(g1, g2)
        else
                g = species.genomes[math.random(1, #species.genomes)]
                child = copyGenome(g)
        end
       
        mutate(child)
       
        return child
end
 
function removeStaleSpecies()
        local survived = {}
 
        for s = 1,#pool.species do
                local species = pool.species[s]
               
                table.sort(species.genomes, function (a,b)
                        return (a.fitness > b.fitness)
                end)
               
                if species.genomes[1].fitness > species.topFitness then
                        species.topFitness = species.genomes[1].fitness
                        species.staleness = 0
                else
                        species.staleness = species.staleness + 1
                end
                if species.staleness < StaleSpecies or species.topFitness >= pool.maxFitness then
                        table.insert(survived, species)
                end
        end
 
        pool.species = survived
end
 
function removeWeakSpecies()
        local survived = {}
 
        local sum = totalAverageFitness()
        for s = 1,#pool.species do
                local species = pool.species[s]
                breed = math.floor(species.averageFitness / sum * Population)
                if breed >= 1 then
                        table.insert(survived, species)
                end
        end
 
        pool.species = survived
end
 
 
function addToSpecies(child)
        local foundSpecies = false
        for s=1,#pool.species do
                local species = pool.species[s]
                if not foundSpecies and sameSpecies(child, species.genomes[1]) then
                        table.insert(species.genomes, child)
                        foundSpecies = true
                end
        end
       
        if not foundSpecies then
                local childSpecies = newSpecies()
                table.insert(childSpecies.genomes, child)
                table.insert(pool.species, childSpecies)
        end
end
 
function newGeneration()
        cullSpecies(false) -- Cull the bottom half of each species
        rankGlobally()
        removeStaleSpecies()
        rankGlobally()
        for s = 1,#pool.species do
                local species = pool.species[s]
                calculateAverageFitness(species)
        end
        removeWeakSpecies()
        local sum = totalAverageFitness()
        local children = {}
        for s = 1,#pool.species do
                local species = pool.species[s]
                breed = math.floor(species.averageFitness / sum * Population) - 1
                for i=1,breed do
                        table.insert(children, breedChild(species))
                end
        end
        cullSpecies(true) -- Cull all but the top member of each species
        while #children + #pool.species < Population do
                local species = pool.species[math.random(1, #pool.species)]
                table.insert(children, breedChild(species))
        end
        for c=1,#children do
                local child = children[c]
                addToSpecies(child)
        end
       
        pool.generation = pool.generation + 1
       
        writeFile("backup." .. pool.generation .. "." .. forms.gettext(saveLoadFile))
end
       

	 * 
	 */

}
