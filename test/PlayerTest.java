import participants.actor.essentials.arsenal.Spell;
import participants.actor.essentials.arsenal.Weapon;
import participants.actor.player.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerTest implements TestUtils {

    private Player player;
    private Weapon weapon;
    private Spell spell;

    @Before
    public void init() {
        this.player = createTestPlayer();
        this.weapon = new Weapon("Epic Zodiac Sword", 80);
        this.spell = new Spell("Dark Orb", 30, 40);
    }

    @Test
    public void testPlayerStats() {
        assertEquals("player1", player.getName());
        assertEquals(1, player.getLevel());
        assertEquals(100, player.getHealth());
        assertEquals(100, player.getMana());
        assertEquals(30, player.getAttack());
        assertEquals(10, player.getDefense());

        assertTrue(player.isAlive());
        assertNull(player.getWeapon());
        assertNull(player.getSpell());
    }

    @Test
    public void testPlayerLevelUpStats() {
        player.takeExperience(110);

        assertEquals(2, player.getLevel());
        assertEquals(110, player.getHealth());
        assertEquals(110, player.getMana());
        assertEquals(35, player.getAttack());
        assertEquals(15, player.getDefense());
    }

    @Test
    public void testPlayerEquipWeapon() {
        player.equipWeapon(weapon);

        assertEquals(weapon.getName(), player.getWeapon().getName());
        assertEquals(weapon.getDamage(), player.getWeapon().getDamage());
    }

    @Test
    public void testPlayerEquipSpell() {
        player.equipSpell(spell);

        assertEquals(spell.getName(), player.getSpell().getName());
        assertEquals(spell.getDamage(), player.getSpell().getDamage());
    }

    @Test
    public void testPlayerDie() {
        player.takeDamage(10000);

        assertEquals(0, player.getHealth());
        assertFalse(player.isAlive());
    }

    @Test
    public void testPlayerNormalAttackNoWeapon() {
        assertEquals(player.getAttack(), player.normalAttack());
    }

    @Test
    public void testPlayerNormalAttackWeapon() {
        player.equipWeapon(weapon);

        assertEquals(player.getAttack() + weapon.getDamage(), player.normalAttack());
    }

//    @Test
//    public void testPlayerSpellAttackNoSpell() {
//        assertEquals(player.getAttack(), player.spellAttack());
//    }
//
//    @Test
//    public void testPlayerSpellAttackSpell() {
//        player.equipSpell(spell);
//
//        assertEquals(player.getAttack() + spell.getDamage(), player.spellAttack());
//    }
//
//    @Test
//    public void testPlayerSpellAttackSpellNoMana() {
//        player.equipSpell(spell);
//
//        // reduce mana
//        player.spellAttack();
//        player.spellAttack();
//        player.spellAttack();
//
//        assertEquals(player.getAttack(), player.spellAttack());
//    }

}
