# Lumbridge Basement Spider Killer by S2147

# Kills spiders in Lumbridge Basement.

def loop():
    if get_combat_style() != settings.fight_mode:
        set_combat_style(settings.fight_mode)
        return 1000
    
    if in_combat():
        return 500
    
    if get_fatigue() > 90:
        use_sleeping_bag()
        return 1000
    
    npc = get_nearest_npc_by_id(ids=settings.npc_ids, in_combat=False, reachable=True)
    if npc != None:
        attack_npc(npc)
        return 500
    
    return 800

def on_progress_report():
    return {"Attack Level": get_max_stat(0),
            "Defence Level": get_max_stat(1),
            "Strength Level": get_max_stat(2),
            "Hitpoints Level": get_max_stat(3),
            "Spiders Killed": get_experience(0) / 6}